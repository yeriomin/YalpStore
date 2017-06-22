package com.github.yeriomin.yalpstore;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.github.yeriomin.yalpstore.model.App;
import com.github.yeriomin.yalpstore.notification.NotificationManagerFactory;

import java.io.File;

public abstract class InstallerAbstract {

    protected Context context;
    protected boolean background;

    static public Intent getOpenApkIntent(Context context, File file) {
        Intent intent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
            intent.setData(FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileprovider", file));
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        return intent;
    }

    abstract public void verifyAndInstall(App app);
    abstract protected void install(App app);

    public InstallerAbstract(Context context) {
        Log.i(getClass().getName(), "Installer chosen");
        this.context = context;
        background = !(context instanceof Activity);
    }

    public void setBackground(boolean background) {
        this.background = background;
    }

    protected AlertDialog getSignatureMismatchDialog() {
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

    protected void notifySignatureMismatch(App app) {
        notifyAndToast(
            R.string.notification_download_complete_signature_mismatch,
            R.string.notification_download_complete_signature_mismatch_toast,
            app
        );
    }

    protected void notifyAndToast(int notificationStringId, int toastStringId, App app) {
        showNotification(notificationStringId, app);
        if (!background) {
            toast(context.getString(toastStringId, app.getDisplayName()));
        }
    }

    private void showNotification(int notificationStringId, App app) {
        File file = Downloader.getApkPath(app.getPackageName(), app.getVersionCode());
        Intent openApkIntent = getOpenApkIntent(context, file);
        NotificationManagerFactory.get(context).show(
            openApkIntent,
            app.getDisplayName(),
            context.getString(notificationStringId)
        );
    }

    protected void toast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}
