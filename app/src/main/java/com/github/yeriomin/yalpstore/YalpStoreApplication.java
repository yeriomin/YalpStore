package com.github.yeriomin.yalpstore;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class YalpStoreApplication extends Application {

    private boolean isBackgroundUpdating = false;
    private List<String> pendingUpdates = new ArrayList<>();

    public boolean isBackgroundUpdating() {
        return isBackgroundUpdating;
    }

    public void setBackgroundUpdating(boolean backgroundUpdating) {
        isBackgroundUpdating = backgroundUpdating;
    }

    public void addPendingUpdate(String packageName) {
        pendingUpdates.add(packageName);
    }

    public void removePendingUpdate(String packageName) {
        pendingUpdates.remove(packageName);
        if (pendingUpdates.isEmpty()) {
            isBackgroundUpdating = false;
            sendBroadcast(new Intent(UpdateAllReceiver.ACTION_UPDATE_COMPLETE));
        }
    }

    public void clearPendingUpdates() {
        pendingUpdates.clear();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        PreferenceManager.setDefaultValues(this, R.xml.settings, false);
        Thread.setDefaultUncaughtExceptionHandler(new YalpStoreUncaughtExceptionHandler(getApplicationContext()));
        registerDownloadReceiver();
        registerInstallReceiver();
    }

    private void registerDownloadReceiver() {
        HandlerThread handlerThread = new HandlerThread("handlerThread");
        handlerThread.start();
        IntentFilter filter = new IntentFilter();
        filter.addAction(DownloadManagerInterface.ACTION_DOWNLOAD_CANCELLED);
        filter.addAction(DownloadManagerInterface.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(new GlobalDownloadReceiver(), filter, null, new Handler(handlerThread.getLooper()));
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void registerInstallReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addDataScheme("package");
        filter.addAction(Intent.ACTION_INSTALL_PACKAGE);
        filter.addAction(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addAction(Intent.ACTION_PACKAGE_FULLY_REMOVED);
        filter.addAction(DetailsInstallReceiver.ACTION_PACKAGE_REPLACED_NON_SYSTEM);
        registerReceiver(new GlobalInstallReceiver(), filter);
    }
}
