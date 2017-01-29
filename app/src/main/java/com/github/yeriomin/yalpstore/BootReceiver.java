package com.github.yeriomin.yalpstore;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent checkerIntent = new Intent(context, UpdateChecker.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, checkerIntent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (isBackgroundCheckEnabled(context)) {
            Log.i(getClass().getName(), "Enabling periodic update checks");
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis(),
                PreferenceActivity.UPDATE_INTERVAL,
                pendingIntent
            );
        }
    }

    private boolean isBackgroundCheckEnabled(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(PreferenceActivity.PREFERENCE_BACKGROUND_UPDATE_CHECK, false);
    }
}
