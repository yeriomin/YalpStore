package in.dragons.galaxy.task.playstore;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import java.util.List;

import in.dragons.galaxy.DownloadManagerAdapter;
import in.dragons.galaxy.DownloadManagerFactory;
import in.dragons.galaxy.DownloadState;
import in.dragons.galaxy.GalaxyApplication;
import in.dragons.galaxy.InstallerAbstract;
import in.dragons.galaxy.InstallerFactory;
import in.dragons.galaxy.NetworkState;
import in.dragons.galaxy.Paths;
import in.dragons.galaxy.PreferenceActivity;
import in.dragons.galaxy.R;
import in.dragons.galaxy.UpdatableAppsActivity;
import in.dragons.galaxy.UpdateAllReceiver;
import in.dragons.galaxy.model.App;
import in.dragons.galaxy.notification.NotificationManagerWrapper;

public class BackgroundUpdatableAppsTask extends UpdatableAppsTask implements CloneableTask {

    private boolean forceUpdate = false;

    public void setForceUpdate(boolean forceUpdate) {
        this.forceUpdate = forceUpdate;
    }

    @Override
    public CloneableTask clone() {
        BackgroundUpdatableAppsTask task = new BackgroundUpdatableAppsTask();
        task.setForceUpdate(forceUpdate);
        task.setContext(context);
        return task;
    }

    @Override
    protected void onPostExecute(List<App> apps) {
        super.onPostExecute(apps);
        if (!success()) {
            return;
        }
        int updatesCount = this.updatableApps.size();
        Log.i(this.getClass().getName(), "Found updates for " + updatesCount + " apps");
        if (updatesCount == 0) {
            context.sendBroadcast(new Intent(UpdateAllReceiver.ACTION_ALL_UPDATES_COMPLETE), null);
            return;
        }
        if (canUpdate()) {
            process(context, updatableApps);
        } else {
            notifyUpdatesFound(context, updatesCount);
        }
    }

    private boolean canUpdate() {
        boolean writePermission = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            writePermission = context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
        if (!writePermission) {
            Log.i(getClass().getSimpleName(), "Write permission not granted");
            return false;
        }
        return forceUpdate ||
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
        GalaxyApplication application = (GalaxyApplication) context.getApplicationContext();
        application.clearPendingUpdates();
        for (App app : apps) {
            application.addPendingUpdate(app.getPackageName());
            if (!Paths.getApkPath(context, app.getPackageName(), app.getVersionCode()).exists()) {
                download(context, app);
            } else if (canInstallInBackground) {
                // Not passing context because it might be an activity
                // and we want it to run in background
                InstallerFactory.get(context.getApplicationContext()).verifyAndInstall(app);
            } else {
                notifyDownloadedAlready(app);
                application.removePendingUpdate(app.getPackageName());
            }
        }
    }

    private void download(Context context, App app) {
        Log.i(getClass().getSimpleName(), "Starting download of update for " + app.getPackageName());
        DownloadState state = DownloadState.get(app.getPackageName());
        state.setApp(app);
        getPurchaseTask(context, app).execute();
    }

    private BackgroundPurchaseTask getPurchaseTask(Context context, App app) {
        BackgroundPurchaseTask task = new BackgroundPurchaseTask();
        task.setApp(app);
        task.setContext(context);
        task.setTriggeredBy(context instanceof Activity
                ? DownloadState.TriggeredBy.UPDATE_ALL_BUTTON
                : DownloadState.TriggeredBy.SCHEDULED_UPDATE
        );
        return task;
    }

    private void notifyUpdatesFound(Context context, int updatesCount) {
        Intent i = new Intent(context, UpdatableAppsActivity.class);
        i.setAction(Intent.ACTION_VIEW);
        new NotificationManagerWrapper(context).show(
                i,
                context.getString(R.string.notification_updates_available_title),
                context.getString(R.string.notification_updates_available_message, updatesCount)
        );
    }

    private void notifyDownloadedAlready(App app) {
        new NotificationManagerWrapper(context).show(
                InstallerAbstract.getOpenApkIntent(context, Paths.getApkPath(context, app.getPackageName(), app.getVersionCode())),
                app.getDisplayName(),
                context.getString(R.string.notification_download_complete)
        );
    }
}
