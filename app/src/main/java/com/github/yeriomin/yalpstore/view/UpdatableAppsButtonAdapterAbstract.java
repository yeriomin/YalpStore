package com.github.yeriomin.yalpstore.view;

import android.view.View;

import com.github.yeriomin.yalpstore.UpdatableAppsActivity;
import com.github.yeriomin.yalpstore.YalpStoreApplication;
import com.github.yeriomin.yalpstore.YalpStorePermissionManager;

public class UpdatableAppsButtonAdapterAbstract extends ButtonAdapter {

    public UpdatableAppsButtonAdapterAbstract(View button) {
        super(button);
    }

    public UpdatableAppsButtonAdapterAbstract init(final UpdatableAppsActivity activity) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YalpStorePermissionManager permissionManager = new YalpStorePermissionManager(activity);
                if (permissionManager.checkPermission()) {
                    activity.launchUpdateAll();
                } else {
                    permissionManager.requestPermission();
                }
            }
        });
        if (((YalpStoreApplication) activity.getApplication()).isBackgroundUpdating()) {
            setUpdating();
        } else {
            setReady();
        }
        return this;
    }

    public UpdatableAppsButtonAdapterAbstract setReady() {
        show();
        enable();
        return this;
    }

    public UpdatableAppsButtonAdapterAbstract setUpdating() {
        show();
        disable();
        return this;
    }
}
