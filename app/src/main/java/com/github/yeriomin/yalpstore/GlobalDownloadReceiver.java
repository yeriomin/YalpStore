package com.github.yeriomin.yalpstore;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.github.yeriomin.yalpstore.model.App;
import com.github.yeriomin.yalpstore.notification.DownloadChecksumService;
import com.github.yeriomin.yalpstore.notification.NotificationManagerWrapper;

import java.io.File;

import static com.github.yeriomin.yalpstore.notification.DownloadChecksumService.PACKAGE_NAME;

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
                Log.e(getClass().getSimpleName(), "Delta patching failed for " + app.getPackageName());
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
        if (shouldInstall(triggeredBy)) {
            Log.i(getClass().getSimpleName(), "Launching installer for " + app.getPackageName());
            InstallerAbstract installer = InstallerFactory.get(context);
            if (triggeredBy.equals(DownloadState.TriggeredBy.DOWNLOAD_BUTTON)
                && (PreferenceActivity.getBoolean(context, PreferenceActivity.PREFERENCE_AUTO_INSTALL)
                    || PreferenceActivity.getBoolean(context, PreferenceActivity.PREFERENCE_DOWNLOAD_INTERNAL_STORAGE)
                )
            ) {
                installer.setBackground(false);
            }
            installer.verifyAndInstall(app);
        } else {
            Log.i(getClass().getSimpleName(), "Notifying about download completion of " + app.getPackageName());
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
        notificationManager.show(
            InstallerAbstract.getCheckAndOpenApkIntent(context, app),
            app.getDisplayName(),
            context.getString(notificationStringId)
        );
        ContextUtil.toast(context.getApplicationContext(), toastStringId, app.getDisplayName());
    }

    private boolean shouldInstall(DownloadState.TriggeredBy triggeredBy) {
        switch (triggeredBy) {
            case DOWNLOAD_BUTTON:
                return PreferenceActivity.getBoolean(context, PreferenceActivity.PREFERENCE_AUTO_INSTALL)
                    || PreferenceActivity.getBoolean(context, PreferenceActivity.PREFERENCE_DOWNLOAD_INTERNAL_STORAGE)
                ;
            case UPDATE_ALL_BUTTON:
                return PreferenceActivity.canInstallInBackground(context);
            case SCHEDULED_UPDATE:
                return PreferenceActivity.canInstallInBackground(context) && PreferenceActivity.getBoolean(context, PreferenceActivity.PREFERENCE_BACKGROUND_UPDATE_INSTALL);
            case MANUAL_DOWNLOAD_BUTTON:
            default:
                return false;
        }
    }
}
