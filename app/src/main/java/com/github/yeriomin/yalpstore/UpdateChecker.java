package com.github.yeriomin.yalpstore;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

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
        UpdatableAppsTask task = new BackgroundUpdatableAppsTask();
        task.setExplicitCheck(context instanceof Activity);
        task.setContext(context);
        task.execute();
    }
}
