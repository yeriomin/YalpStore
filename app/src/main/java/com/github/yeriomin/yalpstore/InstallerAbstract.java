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
import android.util.Log;

import com.github.yeriomin.yalpstore.download.DownloadManager;
import com.github.yeriomin.yalpstore.model.App;
import com.github.yeriomin.yalpstore.notification.IgnoreUpdatesReceiver;
import com.github.yeriomin.yalpstore.notification.NotificationBuilder;
import com.github.yeriomin.yalpstore.notification.NotificationManagerWrapper;
import com.github.yeriomin.yalpstore.notification.SignatureCheckReceiver;
import com.github.yeriomin.yalpstore.view.DialogWrapper;
import com.github.yeriomin.yalpstore.view.DialogWrapperAbstract;

import java.io.File;
import java.security.MessageDigest;

public abstract class InstallerAbstract {

    protected Context context;
    protected boolean background;

    abstract protected void install(App app);

    public static Intent getDownloadChecksumServiceIntent(String packageName) {
        return new Intent(SignatureCheckReceiver.ACTION_CHECK_APK)
            .putExtra(Intent.EXTRA_PACKAGE_NAME, packageName)
        ;
    }

    public InstallerAbstract(Context context) {
        Log.i(getClass().getSimpleName(), "Installer chosen");
        Activity activity = ContextUtil.getActivity(context);
        this.context = null == activity ? context : activity;
        background = !(this.context instanceof Activity);
    }

    public void setBackground(boolean background) {
        this.background = background;
    }

    public void verifyAndInstall(App app) {
        InstallationState.setInstalling(app.getPackageName());
        if (verify(app)) {
            Log.i(getClass().getSimpleName(), "Installing " + app.getPackageName());
            install(app);
        } else {
            ((YalpStoreApplication) context.getApplicationContext()).removePendingUpdate(app.getPackageName());
            InstallationState.setFailure(app.getPackageName());
            sendFailureBroadcast(app.getPackageName());
        }
    }

    protected boolean verify(App app) {
        File apkPath = Paths.getApkPath(context, app.getPackageName(), app.getVersionCode());
        if (!apkPath.exists()) {
            Log.w(getClass().getSimpleName(), apkPath.getAbsolutePath() + " does not exist");
            return false;
        }
        byte[] downloadedFileChecksum = DownloadManager.getApkExpectedHash(app.getPackageName());
        byte[] existingFileChecksum = Util.getFileChecksum(apkPath);
        if (null == downloadedFileChecksum
            || null == existingFileChecksum
            || !MessageDigest.isEqual(downloadedFileChecksum, existingFileChecksum)
        ) {
            Log.e(getClass().getSimpleName(), "Checksums of the existing file and the originally downloaded file are not the same for " + app.getPackageName());
            notifyAndToast(
                R.string.notification_file_verification_failed,
                R.string.notification_file_verification_failed,
                app,
                true
            );
            apkPath.delete();
            return false;
        }
        if (!new ApkSignatureVerifier(context).match(app.getPackageName(), apkPath)) {
            Log.w(getClass().getSimpleName(), "Signature mismatch for " + app.getPackageName());
            notifySignatureMismatch(app);
            return false;
        }
        return true;
    }

    protected void notifyAndToast(int notificationStringId, int toastStringId, App app, boolean intentDetails) {
        showNotification(notificationStringId, app, intentDetails);
        if (!background) {
            ContextUtil.toast(context, toastStringId, app.getDisplayName());
        }
    }

    protected void sendFailureBroadcast(String packageName) {
        Intent intent = new Intent(GlobalInstallReceiver.ACTION_PACKAGE_INSTALLATION_FAILED);
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
                        context.sendBroadcast(getIgnoreIntent(app));
                        Activity activity = ContextUtil.getActivity(context);
                        if (null != activity && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                            activity.invalidateOptionsMenu();
                        }
                        dialog.cancel();
                    }
                }
            );
        }
        return builder.create();
    }

    private void notifySignatureMismatch(App app) {
        if (ContextUtil.isAlive(context)) {
            getSignatureMismatchDialog(app).show();
        } else {
            notifyAndToast(
                R.string.notification_download_complete_signature_mismatch,
                R.string.notification_download_complete_signature_mismatch_toast,
                app,
                false
            );
        }
    }

    private void showNotification(int notificationStringId, App app, boolean intentDetails) {
        NotificationBuilder builder = NotificationManagerWrapper.getBuilder(context)
            .setIntent(intentDetails
                ? DetailsActivity.getDetailsIntent(context, app.getPackageName())
                : getDownloadChecksumServiceIntent(app.getPackageName())
            )
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
        new NotificationManagerWrapper(context).show(app.getPackageName(), builder.build());
    }

    private Intent getIgnoreIntent(App app) {
        Intent intentIgnore = new Intent();
        intentIgnore.setAction(IgnoreUpdatesReceiver.ACTION_IGNORE_UPDATES);
        intentIgnore.putExtra(Intent.EXTRA_PACKAGE_NAME, app.getPackageName());
        intentIgnore.putExtra(IgnoreUpdatesReceiver.VERSION_CODE, app.getVersionCode());
        return intentIgnore;
    }
}
