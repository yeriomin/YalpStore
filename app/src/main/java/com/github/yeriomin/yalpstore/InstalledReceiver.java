package com.github.yeriomin.yalpstore;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class InstalledReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (!action.equals(Intent.ACTION_PACKAGE_INSTALL)
            && !action.equals(Intent.ACTION_PACKAGE_ADDED)
            && !action.equals(Intent.ACTION_PACKAGE_REPLACED)
            && !action.equals(Intent.ACTION_PACKAGE_REMOVED)
            && !action.equals(Intent.ACTION_PACKAGE_FULLY_REMOVED)
            ) {
            return;
        }
        UpdatableAppsActivity.setNeedsUpdate(true);
    }
}
