package com.github.yeriomin.yalpstore;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.github.yeriomin.yalpstore.model.App;

import java.util.List;

public class UpdateChecker extends BroadcastReceiver {

    static public void enable(Context context, int interval) {
        Intent intent = new Intent(context, UpdateChecker.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        if (interval > 0) {
            Log.i(UpdateChecker.class.getName(), "Enabling periodic update checks");
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis(),
                interval,
                pendingIntent
            );
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(getClass().getName(), "Started");
        getTask(context).execute();
    }

    private UpdatableAppsTask getTask(Context context) {
        UpdatableAppsTask task = new UpdatableAppsTask() {
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
                if (explicitCheck || PreferenceActivity.getBoolean(context, PreferenceActivity.PREFERENCE_BACKGROUND_UPDATE_DOWNLOAD)) {
                    process(context, updatableApps);
                } else {
                    createNotification(context, updatesCount);
                }
            }
        };
        task.setExplicitCheck(context instanceof Activity);
        task.setContext(context);
        return task;
    }

    private void process(Context context, List<App> apps) {
        boolean canInstallInBackground = PreferenceActivity.canInstallInBackground(context);
        for (App app: apps) {
            if (!Downloader.getApkPath(app.getPackageName(), app.getVersionCode()).exists()) {
                download(context, app);
            } else if (canInstallInBackground) {
                // Not passing context because it might be an activity
                // and we want it to run in background
                InstallerFactory.get(context.getApplicationContext()).verifyAndInstall(app);
            }
        }
    }

    private void download(Context context, App app) {
        Log.i(getClass().getName(), "Starting download of update for " + app.getPackageName());
        DownloadState state = DownloadState.get(app.getPackageName());
        state.setExplicitInstall(context instanceof Activity);
        state.setApp(app);
        getPurchaseTask(context, app).execute();
    }

    private PurchaseTask getPurchaseTask(Context context, App app) {
        PurchaseTask task = new PurchaseTask();
        task.setApp(app);
        task.setContext(context);
        return task;
    }

    private void createNotification(Context context, int updatesCount) {
        Intent i = new Intent(context, UpdatableAppsActivity.class);
        i.setAction(Intent.ACTION_VIEW);
        new NotificationUtil(context).show(
                i,
                context.getString(R.string.notification_updates_available_title),
                context.getString(R.string.notification_updates_available_message, updatesCount)
        );
    }
}
