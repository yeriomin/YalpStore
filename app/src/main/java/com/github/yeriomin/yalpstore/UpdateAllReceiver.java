package com.github.yeriomin.yalpstore;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.view.View;

import com.github.yeriomin.yalpstore.view.UpdatableAppsButtonAdapter;

public class UpdateAllReceiver extends BroadcastReceiver {

    static public final String ACTION_ALL_UPDATES_COMPLETE = "ACTION_ALL_UPDATES_COMPLETE";
    static public final String ACTION_APP_UPDATE_COMPLETE = "ACTION_APP_UPDATE_COMPLETE";

    static public final String EXTRA_PACKAGE_NAME = "EXTRA_PACKAGE_NAME";
    static public final String EXTRA_UPDATE_ACTUALLY_INSTALLED = "EXTRA_UPDATE_ACTUALLY_INSTALLED";

    private UpdatableAppsActivity activity;

    public UpdateAllReceiver(UpdatableAppsActivity activity) {
        this.activity = activity;
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_ALL_UPDATES_COMPLETE);
        filter.addAction(ACTION_APP_UPDATE_COMPLETE);
        activity.registerReceiver(this, filter);
        initButton();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!ContextUtil.isAlive(activity) || TextUtils.isEmpty(intent.getAction())) {
            return;
        }
        if (intent.getAction().equals(ACTION_ALL_UPDATES_COMPLETE)) {
            ((YalpStoreApplication) activity.getApplication()).setBackgroundUpdating(false);
            initButton();
        } else if (intent.getAction().equals(ACTION_APP_UPDATE_COMPLETE)) {
            processAppUpdate(
                intent.getStringExtra(EXTRA_PACKAGE_NAME),
                intent.getBooleanExtra(EXTRA_UPDATE_ACTUALLY_INSTALLED, false)
            );
        }
    }

    private void initButton() {
        View button = activity.findViewById(R.id.main_button);
        if (null != button) {
            new UpdatableAppsButtonAdapter(button).init(activity);
        }
    }

    private void processAppUpdate(String packageName, boolean installedUpdate) {
        if (installedUpdate) {
            activity.removeApp(packageName);
        }
    }
}
