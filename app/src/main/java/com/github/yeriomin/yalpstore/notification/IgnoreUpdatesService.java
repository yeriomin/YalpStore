package com.github.yeriomin.yalpstore.notification;

import android.app.IntentService;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;

import com.github.yeriomin.yalpstore.BlackWhiteListManager;
import com.github.yeriomin.yalpstore.Downloader;
import com.github.yeriomin.yalpstore.UpdatableAppsActivity;

public class IgnoreUpdatesService extends IntentService {

    static public final String PACKAGE_NAME = "PACKAGE_NAME";
    static public final String VERSION_CODE = "VERSION_CODE";

    public IgnoreUpdatesService() {
        super("IgnoreUpdatesService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String packageName = intent.getStringExtra(PACKAGE_NAME);
        if (TextUtils.isEmpty(packageName)) {
            Log.w(getClass().getName(), "No package name provided in the intent");
            return;
        }
        Log.i(getClass().getName(), "Adding " + packageName + " to ignore list");
        new BlackWhiteListManager(getApplicationContext()).add(packageName);
        cancelNotification(packageName);
        UpdatableAppsActivity.setNeedsUpdate(true);
        Downloader.getApkPath(packageName, intent.getIntExtra(VERSION_CODE, 0)).delete();
    }

    private void cancelNotification(String packageName) {
        PackageManager pm = getApplicationContext().getPackageManager();
        try {
            new NotificationManagerWrapper(getApplicationContext()).cancel(
                pm.getApplicationLabel(pm.getApplicationInfo(packageName, 0)).toString()
            );
        } catch (PackageManager.NameNotFoundException e) {
            // App is not installed
        }
    }
}
