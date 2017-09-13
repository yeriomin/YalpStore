package com.github.yeriomin.yalpstore;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.github.yeriomin.yalpstore.model.App;
import com.github.yeriomin.yalpstore.notification.NotificationManagerWrapper;

import java.io.File;

public class GlobalDownloadReceiver extends DownloadReceiver {

    private NotificationManagerWrapper notificationManager;

    @Override
    protected void process(Context c, Intent i) {
        notificationManager = new NotificationManagerWrapper(c);
        App app = state.getApp();

        DownloadManagerInterface dm = DownloadManagerFactory.get(context);
        if (!dm.finished(downloadId)) {
            return;
        }
        boolean patchSuccess = false;
        if (isDelta(app)) {
            patchSuccess = DeltaPatcherFactory.get(context, app).patch();
            if (!patchSuccess) {
                Log.e(getClass().getName(), "Delta patching failed for " + app.getPackageName());
                return;
            }
        }
        state.setFinished(downloadId);
        if (actionIs(i, DownloadManagerInterface.ACTION_DOWNLOAD_CANCELLED)) {
            return;
        } else if (dm.success(downloadId)) {
            state.setSuccessful(downloadId);
        } else {
            String error = dm.getError(downloadId);
            ContextUtil.toastLong(context.getApplicationContext(), error);
            notificationManager.show(new Intent(), app.getDisplayName(), error);
        }

        if (!state.isEverythingFinished() || !state.isEverythingSuccessful()) {
            return;
        }
        verifyAndInstall(app, state.getTriggeredBy());
        if (patchSuccess) {
            Intent patchingCompleteIntent = new Intent(ACTION_DELTA_PATCHING_COMPLETE);
            patchingCompleteIntent.putExtra(DownloadManagerInterface.EXTRA_DOWNLOAD_ID, downloadId);
            context.sendBroadcast(patchingCompleteIntent);
        }
    }

    private void verifyAndInstall(App app, DownloadState.TriggeredBy triggeredBy) {
        boolean autoInstall = triggeredBy.equals(DownloadState.TriggeredBy.DOWNLOAD_BUTTON) && shouldAutoInstall();
        if (autoInstall
            || (
                needToInstallUpdates()
                && PreferenceActivity.canInstallInBackground(context)
                && (triggeredBy.equals(DownloadState.TriggeredBy.SCHEDULED_UPDATE)
                    || triggeredBy.equals(DownloadState.TriggeredBy.UPDATE_ALL_BUTTON)
                )
            )
        ) {
            Log.i(getClass().getName(), "Launching installer for " + app.getPackageName());
            InstallerAbstract installer = InstallerFactory.get(context);
            if (autoInstall) {
                installer.setBackground(false);
            }
            installer.verifyAndInstall(app);
        } else {
            Log.i(getClass().getName(), "Notifying about download completion of " + app.getPackageName());
            notifyDownloadComplete(app);
            ((YalpStoreApplication) context.getApplicationContext()).removePendingUpdate(app.getPackageName());
        }
    }

    private void notifyDownloadComplete(App app) {
        notifyAndToast(
            R.string.notification_download_complete,
            R.string.notification_download_complete_toast,
            app
        );
    }

    private void notifyAndToast(int notificationStringId, int toastStringId, App app) {
        File file = Paths.getApkPath(context, app.getPackageName(), app.getVersionCode());
        Intent openApkIntent = InstallerAbstract.getOpenApkIntent(context, file);
        notificationManager.show(
            openApkIntent,
            app.getDisplayName(),
            context.getString(notificationStringId)
        );
        ContextUtil.toast(context.getApplicationContext(), toastStringId, app.getDisplayName());
    }

    private boolean needToInstallUpdates() {
        return PreferenceActivity.getBoolean(context, PreferenceActivity.PREFERENCE_BACKGROUND_UPDATE_INSTALL);
    }

    private boolean shouldAutoInstall() {
        return PreferenceActivity.getBoolean(context, PreferenceActivity.PREFERENCE_AUTO_INSTALL);
    }
}
