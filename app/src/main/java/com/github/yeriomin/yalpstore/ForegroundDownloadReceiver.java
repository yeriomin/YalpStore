package com.github.yeriomin.yalpstore;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.lang.ref.WeakReference;

abstract class ForegroundDownloadReceiver extends DownloadReceiver {

    protected WeakReference<YalpStoreActivity> activityRef = new WeakReference<>(null);

    abstract protected void cleanup();
    abstract protected void draw();

    public ForegroundDownloadReceiver(YalpStoreActivity activity) {
        this.activityRef = new WeakReference<>(activity);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_DELTA_PATCHING_COMPLETE);
        filter.addAction(DownloadManagerInterface.ACTION_DOWNLOAD_COMPLETE);
        filter.addAction(DownloadManagerInterface.ACTION_DOWNLOAD_CANCELLED);
        activity.registerReceiver(this, filter);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        YalpStoreActivity activity = activityRef.get();
        if (null == activity || !ContextUtil.isAlive(activity)) {
            return;
        }
        if (null == state) {
            if (actionIs(intent, DownloadManagerInterface.ACTION_DOWNLOAD_CANCELLED)) {
                cleanup();
            } else if (DownloadManagerFactory.get(context).success(downloadId)) {
                state.setSuccessful(downloadId);
            }
        }
    }

    @Override
    protected void process(Context context, Intent intent) {
        if (actionIs(intent, DownloadManagerInterface.ACTION_DOWNLOAD_COMPLETE) && isDelta(state.getApp())) {
            return;
        }
        state.setFinished(downloadId);
        if (DownloadManagerFactory.get(context).success(downloadId) && !actionIs(intent, DownloadManagerInterface.ACTION_DOWNLOAD_CANCELLED)) {
            state.setSuccessful(downloadId);
        }
        if (!state.isEverythingFinished()) {
            return;
        }
        draw();
    }
}
