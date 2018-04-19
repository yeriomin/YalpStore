package com.dragons.aurora;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.dragons.aurora.fragment.PreferenceFragment;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            return;
        }
        UpdateChecker.enable(context.getApplicationContext(), PreferenceFragment.getUpdateInterval(context));
    }
}
