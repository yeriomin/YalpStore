package com.github.yeriomin.yalpstore;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.github.yeriomin.yalpstore.model.App;
import com.github.yeriomin.yalpstore.notification.NotificationManagerFactory;

import java.io.File;

abstract public class InstallerBackground extends InstallerAbstract {

    public InstallerBackground(Context context) {
        super(context);
    }

    @Override
    public void verifyAndInstall(App app) {
        File file = Downloader.getApkPath(app.getPackageName(), app.getVersionCode());
        if (!new ApkSignatureVerifier(context).match(app.getPackageName(), file)) {
            Log.i(getClass().getName(), "Signature mismatch for " + app.getPackageName());
            if (background || !Util.isContextUiCapable(context)) {
                notifySignatureMismatch(app);
            } else {
                getSignatureMismatchDialog().show();
            }
        } else if (background && !new PermissionsComparator(context).isSame(app)) {
            Log.i(getClass().getName(), "New permissions for " + app.getPackageName());
            notifyNewPermissions(app);
        } else {
            Log.i(getClass().getName(), "Installing " + app.getPackageName());
            install(app);
        }
    }

    protected void postInstallationResult(App app, boolean success) {
        String resultString = context.getString(
            success
            ? (app.isInstalled() ? R.string.notification_installation_complete : R.string.details_installed)
            : (app.isInstalled() ? R.string.notification_installation_failed : R.string.details_install_failure)
        );
        if (background) {
            NotificationManagerFactory.get(context).show(new Intent(), app.getDisplayName(), resultString);
        } else {
            toast(resultString);
        }
        app.setInstalled(true);
    }

    private void notifyNewPermissions(App app) {
        notifyAndToast(
            R.string.notification_download_complete_new_permissions,
            R.string.notification_download_complete_new_permissions_toast,
            app
        );
    }
}
