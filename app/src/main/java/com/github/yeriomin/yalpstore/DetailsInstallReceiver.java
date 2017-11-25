package com.github.yeriomin.yalpstore;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.text.TextUtils;

import java.lang.ref.WeakReference;

public class DetailsInstallReceiver extends BroadcastReceiver {

    static public final String ACTION_PACKAGE_REPLACED_NON_SYSTEM = "ACTION_PACKAGE_REPLACED_NON_SYSTEM";
    static public final String ACTION_PACKAGE_INSTALLATION_FAILED = "ACTION_PACKAGE_INSTALLATION_FAILED";

    private WeakReference<DetailsActivity> activityRef = new WeakReference<>(null);
    private String packageName;

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public DetailsInstallReceiver(DetailsActivity activity, String packageName) {
        activityRef = new WeakReference<>(activity);
        this.packageName = packageName;
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addAction(Intent.ACTION_PACKAGE_FULLY_REMOVED);
        filter.addAction(Intent.ACTION_PACKAGE_INSTALL);
        filter.addAction(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        filter.addAction(ACTION_PACKAGE_REPLACED_NON_SYSTEM);
        filter.addAction(ACTION_PACKAGE_INSTALLATION_FAILED);
        activity.registerReceiver(this, filter);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!TextUtils.equals(packageName, intent.getData().getSchemeSpecificPart())) {
            return;
        }
        GlobalInstallReceiver.updateDetails(intent);
        DetailsActivity activity = activityRef.get();
        if (null == activity || !ContextUtil.isAlive(activity)) {
            return;
        }
        activity.redrawDetails(DetailsActivity.app);
    }
}
