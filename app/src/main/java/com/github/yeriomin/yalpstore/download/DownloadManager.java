/*
 * Yalp Store
 * Copyright (C) 2018 Sergey Yeriomin <yeriomin@gmail.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.github.yeriomin.yalpstore.download;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import com.github.yeriomin.playstoreapi.AndroidAppDeliveryData;
import com.github.yeriomin.yalpstore.ContextUtil;
import com.github.yeriomin.yalpstore.DetailsActivity;
import com.github.yeriomin.yalpstore.InstalledApkCopier;
import com.github.yeriomin.yalpstore.InstallerAbstract;
import com.github.yeriomin.yalpstore.InstallerFactory;
import com.github.yeriomin.yalpstore.Paths;
import com.github.yeriomin.yalpstore.PreferenceUtil;
import com.github.yeriomin.yalpstore.R;
import com.github.yeriomin.yalpstore.Util;
import com.github.yeriomin.yalpstore.YalpStoreActivity;
import com.github.yeriomin.yalpstore.YalpStoreApplication;
import com.github.yeriomin.yalpstore.model.App;
import com.github.yeriomin.yalpstore.notification.NotificationManagerWrapper;
import com.github.yeriomin.yalpstore.notification.SignatureCheckReceiver;
import com.github.yeriomin.yalpstore.task.DownloadTask;
import com.github.yeriomin.yalpstore.task.InstallTask;
import com.github.yeriomin.yalpstore.task.InstalledAppsTask;
import com.github.yeriomin.yalpstore.task.PatchTask;

import java.io.File;
import java.security.MessageDigest;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static android.os.Environment.MEDIA_MOUNTED;

public class DownloadManager {

    private static final Map<String, State> downloads = new ConcurrentHashMap<>();

    private Context context;

    public DownloadManager(Context context) {
        this.context = context;
    }

    public void start(App app, AndroidAppDeliveryData deliveryData, State.TriggeredBy triggeredBy) {
        State state = buildState(app, deliveryData, triggeredBy);
        if (downloads.containsKey(app.getPackageName()) && isCancelled(app.getPackageName())) {
            Log.e(getClass().getSimpleName(), "Cancelled " + app.getPackageName());
            return;
        }
        downloads.put(app.getPackageName(), state);
        Activity activity = ContextUtil.getActivity(context);
        if (activity instanceof YalpStoreActivity) {
            DownloadManager.ProgressListener listener = ProgressListenerFactory.get((YalpStoreActivity) activity, app.getPackageName());
            if (null != listener) {
                DownloadManager.addProgressListener(app.getPackageName(), listener);
            }
        }
        if (!enoughSpace(state)) {
            finish(app.getPackageName(), false, Error.INSUFFICIENT_SPACE);
            return;
        }
        if (!isMounted(state.getFiles().values().iterator().next().getRequest().getDestination())) {
            finish(app.getPackageName(), false, Error.DEVICE_NOT_FOUND);
            return;
        }
        for (State.File fileState: state.getFiles().values()) {
            start(state, fileState.getRequest());
        }
    }

    public void complete(String packageName, String type) {
        if (!downloads.containsKey(packageName)) {
            return;
        }
        downloads.get(packageName).getFile(type).setSuccess();
        if (isSuccessful(packageName)) {
            finish(packageName, false, null);
        }
    }

    public void patch(String packageName) {
        if (!downloads.containsKey(packageName) || !downloads.get(packageName).getFiles().containsKey(Request.Type.DELTA.name())) {
            return;
        }
        PatchTask task = new PatchTask();
        task.setContext(context);
        task.setApp(downloads.get(packageName).getApp());
        task.setRequest((RequestDelta) downloads.get(packageName).getFile(Request.Type.DELTA).getRequest());
        task.executeOnExecutorIfPossible();
    }

    public void error(String packageName, Error error) {
        if (null == downloads.get(packageName) || null == downloads.get(packageName).getApp()) {
            return;
        }
        finish(packageName, false, error);
    }

    public void cancel(String packageName) {
        State state = downloads.get(packageName);
        if (null == state) {
            state = new State();
            state.setCancelled(true);
            Activity activity = ContextUtil.getActivity(context);
            if (activity instanceof YalpStoreActivity) {
                state.addProgressListener(ProgressListenerFactory.get(((YalpStoreActivity) activity), packageName));
            }
            downloads.put(packageName, state);
            return;
        }
        for (String type: state.getFiles().keySet()) {
            cancel(packageName, type);
        }
        finish(packageName, true, null);
    }

    private State buildState(App app, AndroidAppDeliveryData deliveryData, State.TriggeredBy triggeredBy) {
        State state = new State();
        state.setApp(app);
        state.setTriggeredBy(triggeredBy);
        for (Request request: RequestBuilder.build(deliveryData, shouldDownloadDelta(app, deliveryData))) {
            request.setPackageName(app.getPackageName());
            request.setDestination(getDestinationFile(request, app.getVersionCode()));
            State.File fileState = new State.File();
            DownloadTask task = new DownloadTask();
            task.setContext(context);
            task.setRequest(request);
            fileState.setTask(task);
            fileState.setRequest(request);
            state.putFile(request.getTypeName(), fileState);
        }
        return state;
    }

    private boolean shouldDownloadDelta(App app, AndroidAppDeliveryData deliveryData) {
        File currentApk = InstalledApkCopier.getCurrentApk(app);
        return app.getVersionCode() > app.getInstalledVersionCode()
            && deliveryData.hasPatchData()
            && null != currentApk
            && PreferenceUtil.getBoolean(context, PreferenceUtil.PREFERENCE_DOWNLOAD_DELTAS)
            && currentApk.exists()
            && hasExpectedChecksum(currentApk, Util.base64StringToByteArray(deliveryData.getPatchData().getBaseSha1()))
        ;
    }

    private void start(State state, Request request) {
        File destination = request.getDestination();
        File downloadDirectory = destination.getParentFile();
        if (!downloadDirectory.exists()) {
            if (!downloadDirectory.mkdirs()) {
                error(request.getPackageName(), Error.DOWNLOAD_DIRECTORY_NOT_ACCESSIBLE);
                return;
            }
        } else if (!downloadDirectory.isDirectory() || !downloadDirectory.canWrite()) {
            error(request.getPackageName(), Error.DOWNLOAD_DIRECTORY_NOT_ACCESSIBLE);
            return;
        }
        State.File fileState = state.getFile(request.getTypeName());
        fileState.setProgressListener(new ProgressNotificationListener(
            context,
            state.getApp().getPackageName(),
            getNotificationTitle(request, state.getApp().getDisplayName())
        ));
        if (destination.exists()) {
            if (destination.length() != request.getSize()
                || Request.Type.DELTA.equals(request.getType())
                || (Request.Type.APK.equals(request.getType()) && !hasExpectedChecksum(destination, request.getHash()))
            ) {
                Log.w(getClass().getSimpleName(), destination + " exists, but the hash is different, deleting: " + destination.delete());
            } else {
                Log.i(getClass().getSimpleName(), destination + " already exists");
                complete(state.getApp().getPackageName(), request.getTypeName());
                return;
            }
        }
        if (null == fileState.getTask()) {
            return;
        }
        fileState.getTask().executeOnExecutorIfPossible();
        new NotificationManagerWrapper(context).show(
            request.getPackageName(),
            new Intent(),
            state.getApp().getDisplayName(),
            context.getString(R.string.notification_download_starting)
        );
    }

    private void cancel(String packageName, String type) {
        State.File state = downloads.get(packageName).getFile(type);
        state.setRunning(false);
        Log.i(getClass().getSimpleName(), "Cancelling " + type + " download for " + packageName);
        if (null != state.getTask()) {
            state.getTask().cancel(false);
            state.setTask(null);
        }
        File destinationFile = state.getRequest().getDestination();
        Log.w(getClass().getSimpleName(), (destinationFile.delete() ? "Deleted " : "Failed to delete ") + destinationFile);
        new NotificationManagerWrapper(context).cancel(packageName);
        downloads.get(packageName).setCancelled(true);
    }

    private void finish(String packageName, boolean cancelled, Error error) {
        State state = downloads.get(packageName);
        boolean shouldInstall = shouldInstall(state.getTriggeredBy());
        if (!shouldInstall) {
            ((YalpStoreApplication) context.getApplicationContext()).removePendingUpdate(packageName);
        }
        if (!cancelled) {
            if (null == error) {
                ContextUtil.toast(context, R.string.notification_download_complete_toast, state.getApp().getDisplayName());
                updateInstalledAppsList(packageName);
                if (shouldInstall) {
                    install(state);
                } else {
                    createNotification(packageName, null);
                }
            } else {
                ContextUtil.toast(context, error.getStringResId());
                createNotification(packageName, error);
            }
        }
        state.complete();
    }

    private void install(State state) {
        InstallerAbstract installer = InstallerFactory.get(context);
        if (State.TriggeredBy.DOWNLOAD_BUTTON.equals(state.getTriggeredBy())
            && (PreferenceUtil.getBoolean(context, PreferenceUtil.PREFERENCE_AUTO_INSTALL)
                || PreferenceUtil.getBoolean(context, PreferenceUtil.PREFERENCE_DOWNLOAD_INTERNAL_STORAGE)
            )
        ) {
            installer.setBackground(false);
        }
        new InstallTask(installer, state.getApp()).execute();
    }

    private void updateInstalledAppsList(String packageName) {
        App app = InstalledAppsTask.getInstalledApp(context.getPackageManager(), packageName);
        if (null != app) {
            if (YalpStoreApplication.installedPackages.containsKey(packageName)) {
                App existingAppRecord = YalpStoreApplication.installedPackages.get(packageName);
                existingAppRecord.setPackageInfo(app.getPackageInfo());
                existingAppRecord.setVersionName(app.getPackageInfo().versionName);
                existingAppRecord.setVersionCode(app.getPackageInfo().versionCode);
            } else {
                YalpStoreApplication.installedPackages.put(packageName, app);
            }
        } else {
            YalpStoreApplication.installedPackages.remove(packageName);
        }
    }

    private boolean shouldInstall(State.TriggeredBy triggeredBy) {
        switch (triggeredBy) {
            case DOWNLOAD_BUTTON:
                return PreferenceUtil.getBoolean(context, PreferenceUtil.PREFERENCE_AUTO_INSTALL)
                    || PreferenceUtil.getBoolean(context, PreferenceUtil.PREFERENCE_DOWNLOAD_INTERNAL_STORAGE)
                ;
            case UPDATE_ALL_BUTTON:
                return PreferenceUtil.canInstallInBackground(context);
            case SCHEDULED_UPDATE:
                return PreferenceUtil.canInstallInBackground(context) && PreferenceUtil.getBoolean(context, PreferenceUtil.PREFERENCE_BACKGROUND_UPDATE_INSTALL);
            case MANUAL_DOWNLOAD_BUTTON:
            default:
                return false;
        }
    }

    private void createNotification(String packageName, Error error) {
        new NotificationManagerWrapper(context).show(
            packageName,
            null == error
                ? new Intent(SignatureCheckReceiver.ACTION_CHECK_APK).putExtra(Intent.EXTRA_PACKAGE_NAME, packageName)
                : DetailsActivity.getDetailsIntent(context, packageName)
            ,
            downloads.get(packageName).getApp().getDisplayName(),
            context.getString(null == error ? R.string.notification_download_complete : error.getStringResId())
        );
    }

    private File getDestinationFile(Request request, int versionCode) {
        if (request instanceof RequestDelta) {
            return Paths.getDeltaPath(context, request.getPackageName(), versionCode);
        } else if (request instanceof RequestObb) {
            return Paths.getObbPath(request.getPackageName(), ((RequestObb) request).getVersionCode(), ((RequestObb) request).isMain());
        } else if (request instanceof RequestSplit) {
            return Paths.getSplitPath(context, request.getPackageName(), versionCode, ((RequestSplit) request).getName());
        } else {
            return Paths.getApkPath(context, request.getPackageName(), versionCode);
        }
    }

    private String getNotificationTitle(Request fileRequest, String displayName) {
        if (fileRequest instanceof RequestObb) {
            return context.getString(
                ((RequestObb) fileRequest).isMain() ? R.string.expansion_file_main : R.string.expansion_file_patch,
                displayName
            );
        } else if (fileRequest instanceof RequestSplit) {
            return context.getString(
                R.string.split_file,
                displayName,
                ((RequestSplit) fileRequest).getName()
            );
        }
        return displayName;
    }

    private boolean isMounted(File file) {
        if (file.getAbsolutePath().startsWith(Paths.getFilesDir(context).getAbsolutePath())) {
            return true;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return !Environment.isExternalStorageRemovable() || MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
        } else {
            return !Environment.isExternalStorageRemovable(file) || MEDIA_MOUNTED.equals(Environment.getExternalStorageState(file));
        }
    }

    private boolean enoughSpace(State state) {
        long bytesNeeded = state.getBytesTotal();
        StatFs stat = new StatFs(Paths.getYalpPath(context).getPath());
        return (long) stat.getBlockSize() * (long) stat.getAvailableBlocks() >= bytesNeeded;
    }

    public static App getApp(String packageName) {
        if (downloads.containsKey(packageName)) {
            return downloads.get(packageName).getApp();
        }
        return null;
    }

    public static byte[] getApkExpectedHash(String packageName) {
        if (!downloads.containsKey(packageName)) {
            return null;
        }
        State.File fileState = downloads.get(packageName).getFile(Request.Type.APK);
        if (null == fileState) {
            fileState = downloads.get(packageName).getFile(Request.Type.DELTA);
        }
        return fileState.getRequest().getHash();
    }

    public static boolean isCancelled(String packageName) {
        return downloads.containsKey(packageName) && downloads.get(packageName).isCancelled();
    }

    public static void unsetCancelled(String packageName) {
        if (downloads.containsKey(packageName)) {
            downloads.get(packageName).setCancelled(false);
        }
    }

    public static void setRunning(String packageName, String type, boolean running) {
        if (!downloads.containsKey(packageName) || null == downloads.get(packageName).getFile(type)) {
            return;
        }
        downloads.get(packageName).getFile(type).setRunning(running);
    }

    public static boolean isRunning(String packageName) {
        if (!downloads.containsKey(packageName) || downloads.get(packageName).getFiles().isEmpty()) {
            return false;
        }
        for (State.File file: downloads.get(packageName).getFiles().values()) {
            if (file.isRunning()) {
                return true;
            }
        }
        return false;
    }

    public static boolean isSuccessful(String packageName) {
        if (!downloads.containsKey(packageName) || downloads.get(packageName).getFiles().isEmpty()) {
            return false;
        }
        for (State.File file: downloads.get(packageName).getFiles().values()) {
            if (!file.isSuccess()) {
                return false;
            }
        }
        return true;
    }

    public static void addProgressListener(String packageName, ProgressListener listener) {
        if (downloads.containsKey(packageName)) {
            downloads.get(packageName).addProgressListener(listener);
        }
    }

    public static void setBytesDownloaded(String packageName, String type, int bytesDownloaded) {
        if (downloads.containsKey(packageName)) {
            downloads.get(packageName).setBytesDownloaded(type, bytesDownloaded);
        }
    }

    public static void resumeAll() {
        for (State state: downloads.values()) {
            if (state.isCancelled()) {
                continue;
            }
            for (State.File request: state.getFiles().values()) {
                DownloadTask task = request.getTask();
                if (null != task && task.isPaused() && !task.isCancelled()) {
                    task.resume();
                }
            }
        }
    }

    static private boolean hasExpectedChecksum(File file, byte[] expectedChecksum) {
        byte[] existingChecksum = Util.getFileChecksum(file);
        boolean match = MessageDigest.isEqual(expectedChecksum, existingChecksum);
        if (!match) {
            Log.w(DownloadManager.class.getSimpleName(), file + " does not match expected sha1 hash");
        }
        return match;
    }

    public interface ProgressListener {

        void onProgress(long bytesDownloaded, long bytesTotal);
        void onCompletion();
    }

    public enum Error {

        UNKNOWN,
        FILE_ERROR,
        HTTP_DATA_ERROR,
        INSUFFICIENT_SPACE,
        DEVICE_NOT_FOUND,
        CANNOT_RESUME,
        DELTA_FAILED,
        DOWNLOAD_DIRECTORY_NOT_ACCESSIBLE,
        SPLITS_NOT_SUPPORTED;

        public int getStringResId() {
            switch (this) {
                case FILE_ERROR:
                    return R.string.download_manager_ERROR_FILE_ERROR;
                case HTTP_DATA_ERROR:
                    return R.string.download_manager_ERROR_HTTP_DATA_ERROR;
                case INSUFFICIENT_SPACE:
                    return R.string.download_manager_ERROR_INSUFFICIENT_SPACE;
                case DEVICE_NOT_FOUND:
                    return R.string.download_manager_ERROR_DEVICE_NOT_FOUND;
                case CANNOT_RESUME:
                    return R.string.download_manager_ERROR_CANNOT_RESUME;
                case DELTA_FAILED:
                    return R.string.download_manager_ERROR_DELTA_FAILED;
                case DOWNLOAD_DIRECTORY_NOT_ACCESSIBLE:
                    return R.string.error_downloads_directory_not_writable;
                case SPLITS_NOT_SUPPORTED:
                    return R.string.download_manager_SPLITS_NOT_SUPPORTED;
                case UNKNOWN:
                default:
                    return R.string.download_manager_ERROR_UNKNOWN;
            }
        }
    }
}
