package com.github.yeriomin.yalpstore;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.github.yeriomin.yalpstore.model.App;
import com.github.yeriomin.yalpstore.notification.NotificationManagerWrapper;

import java.util.List;

class BackgroundUpdatableAppsTask extends UpdatableAppsTask {

    @Override
    protected void onPostExecute(Throwable e) {
        super.onPostExecute(e);
        if (null != e) {
            return;
        }
        int updatesCount = this.updatableApps.size();
        Log.i(this.getClass().getName(), "Found updates for " + updatesCount + " apps");
        if (updatesCount == 0) {
            return;
        }
        if (canUpdate()) {
            process(context, updatableApps);
        } else {
            createNotification(context, updatesCount);
        }
    }

    private boolean canUpdate() {
        return explicitCheck ||
            (PreferenceActivity.getBoolean(context, PreferenceActivity.PREFERENCE_BACKGROUND_UPDATE_DOWNLOAD)
                && (DownloadManagerFactory.get(context) instanceof DownloadManagerAdapter
                    || !PreferenceActivity.getBoolean(context, PreferenceActivity.PREFERENCE_BACKGROUND_UPDATE_WIFI_ONLY)
                    || !NetworkState.isMetered(context)
                )
            )
        ;
    }

    private void process(Context context, List<App> apps) {
        boolean canInstallInBackground = PreferenceActivity.canInstallInBackground(context);
        YalpStoreApplication application = (YalpStoreApplication) context.getApplicationContext();
        application.clearPendingUpdates();
        for (App app: apps) {
            application.addPendingUpdate(app.getPackageName());
            if (!Paths.getApkPath(app.getPackageName(), app.getVersionCode()).exists()) {
                download(context, app);
            } else if (canInstallInBackground) {
                // Not passing context because it might be an activity
                // and we want it to run in background
                InstallerFactory.get(context.getApplicationContext()).verifyAndInstall(app);
            } else {
                application.removePendingUpdate(app.getPackageName());
            }
        }
    }

    private void download(Context context, App app) {
        Log.i(getClass().getName(), "Starting download of update for " + app.getPackageName());
        DownloadState state = DownloadState.get(app.getPackageName());
        state.setApp(app);
        getPurchaseTask(context, app).execute();
    }

    private PurchaseTask getPurchaseTask(Context context, App app) {
        PurchaseTask task = new PurchaseTask();
        task.setApp(app);
        task.setContext(context);
        task.setTriggeredBy(context instanceof Activity
            ? DownloadState.TriggeredBy.UPDATE_ALL_BUTTON
            : DownloadState.TriggeredBy.SCHEDULED_UPDATE
        );
        return task;
    }

    private void createNotification(Context context, int updatesCount) {
        Intent i = new Intent(context, UpdatableAppsActivity.class);
        i.setAction(Intent.ACTION_VIEW);
        new NotificationManagerWrapper(context).show(
            i,
            context.getString(R.string.notification_updates_available_title),
            context.getString(R.string.notification_updates_available_message, updatesCount)
        );
    }
}
