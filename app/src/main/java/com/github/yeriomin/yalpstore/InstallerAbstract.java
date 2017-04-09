package com.github.yeriomin.yalpstore;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.Toast;

import com.github.yeriomin.yalpstore.model.App;

import java.io.File;

public abstract class InstallerAbstract {

    protected Context context;
    protected boolean background;

    abstract protected void install(App app);

    public InstallerAbstract(Context context) {
        this.context = context;
        this.background = !(context instanceof Activity);
    }

    public void verifyAndInstall(App app) {
        File file = Downloader.getApkPath(app.getPackageName(), app.getVersionCode());
        if (!new ApkSignatureVerifier(context).match(app.getPackageName(), file)) {
            if (background) {
                notifySignatureMismatch(app);
            } else {
                getSignatureMismatchDialog().show();
            }
        } else if (background && !new PermissionsComparator(context).isSame(app)) {
            notifyNewPermissions(app);
        } else {
            install(app);
        }
    }

    private AlertDialog getSignatureMismatchDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
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
        return builder.create();
    }

    private void notifySignatureMismatch(App app) {
        notifyAndToast(
            R.string.notification_download_complete_signature_mismatch,
            R.string.notification_download_complete_signature_mismatch_toast,
            app
        );
    }

    private void notifyNewPermissions(App app) {
        notifyAndToast(
            R.string.notification_download_complete_new_permissions,
            R.string.notification_download_complete_new_permissions_toast,
            app
        );
    }

    private void notifyAndToast(int notificationStringId, int toastStringId, App app) {
        showNotification(notificationStringId, app);
        toast(context.getString(toastStringId, app.getDisplayName()));
    }

    protected void showNotification(int notificationStringId, App app) {
        File file = Downloader.getApkPath(app.getPackageName(), app.getVersionCode());
        Intent openApkIntent = DownloadOrInstallFragment.getOpenApkIntent(context, file);
        new NotificationUtil(context).show(
            openApkIntent,
            app.getDisplayName(),
            context.getString(notificationStringId)
        );
    }

    protected void toast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}
