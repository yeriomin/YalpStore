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

package com.github.yeriomin.yalpstore;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.text.TextUtils;
import android.util.Log;

import com.github.yeriomin.yalpstore.download.DownloadManager;
import com.github.yeriomin.yalpstore.model.App;
import com.github.yeriomin.yalpstore.model.Event;
import com.github.yeriomin.yalpstore.notification.NotificationManagerWrapper;
import com.github.yeriomin.yalpstore.task.InstalledAppsTask;
import com.github.yeriomin.yalpstore.task.playstore.ChangelogTask;

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

public class GlobalInstallReceiver extends PackageSpecificReceiver {

    static public final String ACTION_INSTALL_UI_UPDATE = "ACTION_INSTALL_UI_UPDATE";
    static public final String ACTION_PACKAGE_INSTALLATION_FAILED = "ACTION_PACKAGE_INSTALLATION_FAILED";

    static private final long INTENT_HASH_TTL = 5000;
    static private final Map<Long, Long> intentHashes = new ConcurrentHashMap<>();

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        long intentHash = getIntentHash(intent);
        if (!isProperIntent(intent) || intentHashes.keySet().contains(intentHash)) {
            cleanupIntentHashes();
            return;
        }
        intentHashes.put(intentHash, System.currentTimeMillis());
        String action = intent.getAction();
        packageName = intent.getData().getSchemeSpecificPart();
        Log.i(getClass().getSimpleName(), "Finished installation (" + action + ") of " + packageName);
        try {
            getEventTask(context, packageName, action).executeOnExecutorIfPossible();
        } catch (Throwable e) {
            // No failure to log an event is important enough to let the app crash
            Log.e(getClass().getSimpleName(), "Could not log event: " + e.getClass().getName() + " " + e.getMessage());
        }
        updateInstalledAppsList(context, packageName);
        context.sendBroadcast(new Intent(ACTION_INSTALL_UI_UPDATE).putExtra(Intent.EXTRA_PACKAGE_NAME, packageName));
        boolean actionIsInstall = actionIsInstall(action);
        ((YalpStoreApplication) context.getApplicationContext()).removePendingUpdate(packageName, actionIsInstall);
        if (!actionIsInstall) {
            if (!ACTION_PACKAGE_INSTALLATION_FAILED.equals(action)) {
                new NotificationManagerWrapper(context).cancel(packageName);
            }
            return;
        }
        BlackWhiteListManager manager = new BlackWhiteListManager(context);
        if (wasInstalled(context, packageName) && needToAutoWhitelist(context) && !manager.isBlack()) {
            Log.i(getClass().getSimpleName(), "Whitelisting " + packageName);
            manager.add(packageName);
        }
        App app = YalpStoreApplication.installedPackages.get(packageName);
        if (needToRemoveApk(context)) {
            removeApks(context, app);
        }
        if (installationMethodIsDefault(context)) {
            new NotificationManagerWrapper(context).cancel(packageName);
        }
    }

    static private boolean isProperIntent(Intent intent) {
        return !(null == intent
            || TextUtils.isEmpty(intent.getAction())
            || null == intent.getData()
            || TextUtils.isEmpty(intent.getData().getSchemeSpecificPart())
            || ((intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED) || intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED))
                && intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)
            )
        );
    }

    /**
     * Root and privileged methods create two identical intents upon installation on some devices
     * So to prevent double work on them and double event records, we need to filter them
     *
     */
    static private long getIntentHash(Intent i) {
        StringBuilder sb = new StringBuilder();
        sb.append(i.getAction()).append("|").append(i.getDataString()).append("|");
        if (null != i.getExtras() && !i.getExtras().isEmpty()) {
            for (String key: i.getExtras().keySet()) {
                sb.append(key).append("=").append(i.getExtras().get(key)).append(";");
            }
        }
        byte[] bytes = sb.toString().getBytes();
        Checksum checksum = new CRC32();
        checksum.update(bytes, 0, bytes.length);
        return checksum.getValue();
    }

    static private void cleanupIntentHashes() {
        Iterator<Long> iterator = intentHashes.keySet().iterator();
        while (iterator.hasNext()) {
            Long currentIntentHash = iterator.next();
            if (intentHashes.get(currentIntentHash) + INTENT_HASH_TTL < System.currentTimeMillis()) {
                intentHashes.remove(currentIntentHash);
            }
        }
    }

    static private ChangelogTask getEventTask(Context context, String packageName, String action) {
        App app = YalpStoreApplication.installedPackages.get(packageName);
        if (null == app) {
            app = new App();
            app.setPackageInfo(new PackageInfo());
            app.getPackageInfo().packageName = packageName;
        }
        ChangelogTask task = new ChangelogTask();
        task.setContext(context);
        task.setApp(app);
        switch (action) {
            case ACTION_PACKAGE_INSTALLATION_FAILED:
                task.setEventType(Event.TYPE.INSTALLATION, false);
                break;
            case Intent.ACTION_PACKAGE_FULLY_REMOVED:
            case Intent.ACTION_PACKAGE_REMOVED:
                task.setEventType(Event.TYPE.REMOVAL, true);
                break;
            case Intent.ACTION_PACKAGE_INSTALL:
            case Intent.ACTION_PACKAGE_ADDED:
            case Intent.ACTION_PACKAGE_REPLACED:
                task.setEventType(app.getInstalledVersionCode() > 0 ? Event.TYPE.UPDATE : Event.TYPE.INSTALLATION, true);
                break;
        }
        return task;
    }

    static private void updateInstalledAppsList(Context context, String packageName) {
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

    static public boolean actionIsInstall(String action) {
        return action.equals(Intent.ACTION_PACKAGE_INSTALL)
            || action.equals(Intent.ACTION_PACKAGE_ADDED)
            || action.equals(Intent.ACTION_PACKAGE_REPLACED)
        ;
    }

    static private boolean needToRemoveApk(Context context) {
        return PreferenceUtil.getBoolean(context, PreferenceUtil.PREFERENCE_DELETE_APK_AFTER_INSTALL)
            || PreferenceUtil.getBoolean(context, PreferenceUtil.PREFERENCE_DOWNLOAD_INTERNAL_STORAGE)
        ;
    }

    static private boolean needToAutoWhitelist(Context context) {
        return PreferenceUtil.getBoolean(context, PreferenceUtil.PREFERENCE_AUTO_WHITELIST);
    }

    static private boolean wasInstalled(Context context, String packageName) {
        return InstallationState.isInstalled(packageName)
            || (installationMethodIsDefault(context) && DownloadManager.isSuccessful(packageName))
        ;
    }

    static private void removeApks(Context context, App app) {
        for (File apk: Paths.getApkAndSplits(context, app.getPackageName(), app.getVersionCode())) {
            if (apk.exists()) {
                Log.i(GlobalInstallReceiver.class.getSimpleName(), apk + " " + (apk.delete() ? "" : "FAILED to be ") + "deleted");
            }
        }
    }

    static private boolean installationMethodIsDefault(Context context) {
        return PreferenceUtil.getString(context, PreferenceUtil.INSTALLATION_METHOD_DEFAULT).equals(PreferenceUtil.INSTALLATION_METHOD_DEFAULT);
    }
}
