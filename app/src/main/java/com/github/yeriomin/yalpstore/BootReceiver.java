package com.github.yeriomin.yalpstore;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            return;
        }
        UpdateChecker.enable(context.getApplicationContext(), getUpdateInterval(context));
    }

    static private int getUpdateInterval(Context context) {
        return Util.parseInt(
            PreferenceManager.getDefaultSharedPreferences(context).getString(
                PreferenceUtil.PREFERENCE_BACKGROUND_UPDATE_INTERVAL,
                "-1"
            ),
            -1
        );
    }
}
