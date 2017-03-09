package com.github.yeriomin.yalpstore;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.github.yeriomin.yalpstore.model.App;

import java.io.IOException;
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
                int updatesCount = this.apps.size();
                Log.i(this.getClass().getName(), "Found updates for " + updatesCount + " apps");
                if (updatesCount == 0) {
                    return;
                }
                if (needToInstallUpdates(context)) {
                    download(context, apps);
                } else {
                    createNotification(context, updatesCount);
                }
            }
        };
        task.setContext(context);
        return task;
    }

    private void download(Context context, List<App> apps) {
        for (App app: apps) {
            Log.i(getClass().getName(), "Starting download of update for " + app.getPackageName());
            DownloadState state = DownloadState.get(app.getPackageName());
            state.setApp(app);
            state.setBackground(true);
            getPurchaseTask(context, app).execute();
        }
    }

    private PurchaseTask getPurchaseTask(Context context, App app) {
        PurchaseTask task = new PurchaseTask();
        task.setApp(app);
        task.setContext(context);
        return task;
    }

    private boolean needToInstallUpdates(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(PreferenceActivity.PREFERENCE_BACKGROUND_UPDATE_INSTALL, false);
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
