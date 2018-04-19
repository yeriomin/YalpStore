package com.dragons.aurora;

import android.content.Context;
import android.util.Log;

import com.dragons.aurora.activities.DetailsActivity;
import com.dragons.aurora.model.App;
import com.dragons.aurora.notification.NotificationManagerWrapper;

abstract public class InstallerBackground extends InstallerAbstract {

    private boolean wasInstalled;

    public InstallerBackground(Context context) {
        super(context);
    }

    @Override
    public boolean verify(App app) {
        if (!super.verify(app)) {
            return false;
        }
        if (background && !new PermissionsComparator(context).isSame(app)) {
            Log.i(getClass().getSimpleName(), "New permissions for " + app.getPackageName());
            ((AuroraApplication) context.getApplicationContext()).removePendingUpdate(app.getPackageName());
            notifyNewPermissions(app);
            return false;
        }
        wasInstalled = app.isInstalled();
        return true;
    }

    protected void postInstallationResult(App app, boolean success) {
        String resultString = context.getString(
                success
                        ? (wasInstalled ? R.string.notification_installation_complete : R.string.details_installed)
                        : (wasInstalled ? R.string.notification_installation_failed : R.string.details_install_failure)
        );
        if (background) {
            new NotificationManagerWrapper(context).show(DetailsActivity.getDetailsIntent(context, app.getPackageName()), app.getDisplayName(), resultString);
        } else {
            ContextUtil.toastLong(context, resultString);
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
