package com.github.yeriomin.yalpstore;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.github.yeriomin.yalpstore.model.App;
import com.github.yeriomin.yalpstore.notification.NotificationManagerWrapper;

abstract public class InstallerBackground extends InstallerAbstract {

    public InstallerBackground(Context context) {
        super(context);
    }

    @Override
    public boolean verify(App app) {
        if (!super.verify(app)) {
            return false;
        }
        if (background && !new PermissionsComparator(context).isSame(app)) {
            Log.i(getClass().getName(), "New permissions for " + app.getPackageName());
            notifyNewPermissions(app);
            return false;
        }
        return true;
    }

    protected void postInstallationResult(App app, boolean success) {
        String resultString = context.getString(
            success
            ? (app.isInstalled() ? R.string.notification_installation_complete : R.string.details_installed)
            : (app.isInstalled() ? R.string.notification_installation_failed : R.string.details_install_failure)
        );
        if (background) {
            new NotificationManagerWrapper(context).show(new Intent(), app.getDisplayName(), resultString);
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
