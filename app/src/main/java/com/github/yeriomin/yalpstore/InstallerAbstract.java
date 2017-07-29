package com.github.yeriomin.yalpstore;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.github.yeriomin.yalpstore.model.App;
import com.github.yeriomin.yalpstore.notification.IgnoreUpdatesService;
import com.github.yeriomin.yalpstore.notification.NotificationManagerWrapper;

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

    abstract protected void install(App app);

    public InstallerAbstract(Context context) {
        Log.i(getClass().getName(), "Installer chosen");
        this.context = context;
        background = !(context instanceof Activity);
    }

    public void setBackground(boolean background) {
        this.background = background;
    }

    public void verifyAndInstall(App app) {
        if (verify(app)) {
            Log.i(getClass().getName(), "Installing " + app.getPackageName());
            install(app);
        } else {
            context.sendBroadcast(new Intent(DetailsInstallReceiver.ACTION_PACKAGE_INSTALLATION_FAILED));
        }
    }

    protected boolean verify(App app) {
        if (!new ApkSignatureVerifier(context).match(
            app.getPackageName(),
            Paths.getApkPath(app.getPackageName(), app.getVersionCode())
        )) {
            Log.i(getClass().getName(), "Signature mismatch for " + app.getPackageName());
            ((YalpStoreApplication) context.getApplicationContext()).removePendingUpdate(app.getPackageName());
            if (Util.isContextUiCapable(context)) {
                getSignatureMismatchDialog(app).show();
            } else {
                notifySignatureMismatch(app);
            }
            return false;
        }
        return true;
    }

    protected void notifyAndToast(int notificationStringId, int toastStringId, App app) {
        showNotification(notificationStringId, app);
        if (!background) {
            toast(context.getString(toastStringId, app.getDisplayName()));
        }
    }

    protected void toast(String message) {
        Toast.makeText(context.getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    private AlertDialog getSignatureMismatchDialog(final App app) {
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
            .setNegativeButton(
                R.string.action_ignore,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        context.startService(getIgnoreIntent(app));
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

    private void showNotification(int notificationStringId, App app) {
        File file = Paths.getApkPath(app.getPackageName(), app.getVersionCode());
        Intent openApkIntent = getOpenApkIntent(context, file);
        Notification notification = NotificationManagerWrapper.getBuilder(context)
            .setIntent(openApkIntent)
            .setTitle(app.getDisplayName())
            .setMessage(context.getString(notificationStringId))
            .addAction(
                android.R.drawable.ic_menu_close_clear_cancel,
                R.string.action_ignore,
                PendingIntent.getService(context, 0, getIgnoreIntent(app), PendingIntent.FLAG_UPDATE_CURRENT)
            )
            .build()
        ;
        new NotificationManagerWrapper(context).show(app.getDisplayName(), notification);
    }

    private Intent getIgnoreIntent(App app) {
        Intent intentIgnore = new Intent(context, IgnoreUpdatesService.class);
        intentIgnore.putExtra(IgnoreUpdatesService.PACKAGE_NAME, app.getPackageName());
        intentIgnore.putExtra(IgnoreUpdatesService.VERSION_CODE, app.getVersionCode());
        return intentIgnore;
    }
}
