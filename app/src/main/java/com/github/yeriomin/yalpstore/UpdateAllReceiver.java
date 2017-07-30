package com.github.yeriomin.yalpstore;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.Button;

public class UpdateAllReceiver extends BroadcastReceiver {

    static public final String ACTION_UPDATE_COMPLETE = "ACTION_UPDATE_COMPLETE";

    private Button button;

    public UpdateAllReceiver(UpdatableAppsActivity activity) {
        button = activity.findViewById(R.id.update_all);
        IntentFilter filter = new IntentFilter();
        filter.addAction(UpdateAllReceiver.ACTION_UPDATE_COMPLETE);
        activity.registerReceiver(this, filter);
        if (!((YalpStoreApplication) activity.getApplication()).isBackgroundUpdating()) {
            onReceive(activity, new Intent());
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (null != button) {
            button.setEnabled(true);
            button.setText(R.string.list_update_all);
        }
    }
}
