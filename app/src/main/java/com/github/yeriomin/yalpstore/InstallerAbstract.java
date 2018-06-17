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

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.util.Log;

import com.github.yeriomin.yalpstore.model.App;
import com.github.yeriomin.yalpstore.notification.DownloadChecksumService;
import com.github.yeriomin.yalpstore.notification.IgnoreUpdatesService;
import com.github.yeriomin.yalpstore.notification.NotificationBuilder;
import com.github.yeriomin.yalpstore.notification.NotificationManagerWrapper;
import com.github.yeriomin.yalpstore.view.DialogWrapper;
import com.github.yeriomin.yalpstore.view.DialogWrapperAbstract;

import java.io.File;
import java.security.MessageDigest;

public abstract class InstallerAbstract {

    protected Context context;
    protected boolean background;

    static public Intent getCheckAndOpenApkIntent(Context context, App app) {
        return PreferenceUtil.getBoolean(context, PreferenceUtil.PREFERENCE_DOWNLOAD_INTERNAL_STORAGE)
            ? getDownloadChecksumServiceIntent(context, app)
            : getOpenApkIntent(context, app)
        ;
    }

    static private Intent getDownloadChecksumServiceIntent(Context context, App app) {
        return new Intent(context, DownloadChecksumService.class)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            .setAction(Intent.ACTION_INSTALL_PACKAGE + System.currentTimeMillis())
            .putExtra(DownloadChecksumService.PACKAGE_NAME, app.getPackageName())
        ;
    }

    static public Intent getOpenApkIntent(Context context, App app) {
        Intent intent;
        File file = Paths.getApkPath(context, app.getPackageName(), app.getVersionCode());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
            intent.setData(FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileprovider", file));
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    abstract protected void install(App app);

    public InstallerAbstract(Context context) {
        Log.i(getClass().getSimpleName(), "Installer chosen");
        this.context = context;
        background = !(context instanceof Activity);
    }

    public void setBackground(boolean background) {
        this.background = background;
    }

    public void verifyAndInstall(App app) {
        if (verify(app)) {
            Log.i(getClass().getSimpleName(), "Installing " + app.getPackageName());
            install(app);
        } else {
            sendBroadcast(app.getPackageName(), false);
        }
    }

    protected boolean verify(App app) {
        File apkPath = Paths.getApkPath(context, app.getPackageName(), app.getVersionCode());
        if (!apkPath.exists()) {
            Log.w(getClass().getSimpleName(), apkPath.getAbsolutePath() + " does not exist");
            return false;
        }
        if (!new ApkSignatureVerifier(context).match(app.getPackageName(), apkPath)) {
            Log.w(getClass().getSimpleName(), "Signature mismatch for " + app.getPackageName());
            ((YalpStoreApplication) context.getApplicationContext()).removePendingUpdate(app.getPackageName());
            if (ContextUtil.isAlive(context)) {
                getSignatureMismatchDialog(app).show();
            } else {
                notifySignatureMismatch(app);
            }
            return false;
        }
        if (PreferenceUtil.getBoolean(context, PreferenceUtil.PREFERENCE_DOWNLOAD_INTERNAL_STORAGE)) {
            byte[] downloadedFileChecksum = DownloadState.get(app.getPackageName()).getApkChecksum();
            byte[] existingFileChecksum = Util.getFileChecksum(apkPath);
            if (null == downloadedFileChecksum
                || null == existingFileChecksum
                || !MessageDigest.isEqual(downloadedFileChecksum, existingFileChecksum)
            ) {
                Log.e(getClass().getSimpleName(), "Checksums of the existing file and the originally downloaded file are not the same for " + app.getPackageName());
                ((YalpStoreApplication) context.getApplicationContext()).removePendingUpdate(app.getPackageName());
                notifyAndToast(
                    R.string.notification_file_verification_failed,
                    R.string.notification_file_verification_failed,
                    app
                );
                apkPath.delete();
                return false;
            }
        }
        return true;
    }

    protected void notifyAndToast(int notificationStringId, int toastStringId, App app) {
        showNotification(notificationStringId, app);
        if (!background) {
            ContextUtil.toast(context, toastStringId, app.getDisplayName());
        }
    }

    protected void sendBroadcast(String packageName, boolean success) {
        Intent intent = new Intent(
            success
            ? GlobalInstallReceiver.ACTION_PACKAGE_REPLACED_NON_SYSTEM
            : GlobalInstallReceiver.ACTION_PACKAGE_INSTALLATION_FAILED
        );
        intent.setData(new Uri.Builder().scheme("package").opaquePart(packageName).build());
        context.sendBroadcast(intent);
    }

    private DialogWrapperAbstract getSignatureMismatchDialog(final App app) {
        DialogWrapperAbstract builder = new DialogWrapper((Activity) context);
        builder
            .setMessage(R.string.details_signature_mismatch)
            .setPositiveButton(
                android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                }
            )
        ;
        if (new BlackWhiteListManager(context).isUpdatable(app.getPackageName())) {
            builder.setNegativeButton(
                R.string.action_ignore,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        context.startService(getIgnoreIntent(app));
                        dialog.cancel();
                    }
                }
            );
        }
        return builder.create();
    }

    private void notifySignatureMismatch(App app) {
        notifyAndToast(
            R.string.notification_download_complete_signature_mismatch,
            R.string.notification_download_complete_signature_mismatch_toast,
            app
        );
    }

    private void showNotification(int notificationStringId, App app) {
        NotificationBuilder builder = NotificationManagerWrapper.getBuilder(context)
            .setIntent(getCheckAndOpenApkIntent(context, app))
            .setTitle(app.getDisplayName())
            .setMessage(context.getString(notificationStringId))
        ;
        if (new BlackWhiteListManager(context).isUpdatable(app.getPackageName())) {
            builder.addAction(
                R.drawable.ic_cancel,
                R.string.action_ignore,
                PendingIntent.getService(context, 0, getIgnoreIntent(app), PendingIntent.FLAG_UPDATE_CURRENT)
            );
        }
        new NotificationManagerWrapper(context).show(app.getDisplayName(), builder.build());
    }

    private Intent getIgnoreIntent(App app) {
        Intent intentIgnore = new Intent(context, IgnoreUpdatesService.class);
        intentIgnore.putExtra(IgnoreUpdatesService.PACKAGE_NAME, app.getPackageName());
        intentIgnore.putExtra(IgnoreUpdatesService.VERSION_CODE, app.getVersionCode());
        return intentIgnore;
    }
}
