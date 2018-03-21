package com.github.yeriomin.yalpstore;

import android.content.Context;
import android.content.Intent;

import com.github.yeriomin.yalpstore.view.AppBadge;

public class AppListDownloadReceiver extends ForegroundDownloadReceiver {

    public AppListDownloadReceiver(AppListActivity activity) {
        super(activity);
    }

    @Override
    protected void cleanup() {
        draw();
    }

    @Override
    protected void draw() {
        AppBadge appBadge = getAppBadge();
        if (null != appBadge) {
            appBadge.redrawMoreButton();
        }
    }

    @Override
    protected void process(Context context, Intent intent) {
        AppListActivity activity = (AppListActivity) activityRef.get();
        if (!activity.getListedPackageNames().contains(state.getApp().getPackageName())) {
            return;
        }
        super.process(context, intent);
    }

    private AppBadge getAppBadge() {
        if (null == activityRef.get() || null == state || null == state.getApp()) {
            return null;
        }
        return (AppBadge) ((AppListActivity) activityRef.get()).getListItem(state.getApp().getPackageName());
    }
}
