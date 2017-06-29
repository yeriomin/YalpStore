package com.github.yeriomin.yalpstore;

import android.app.Application;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.HandlerThread;
import android.preference.PreferenceManager;

public class YalpStoreApplication extends Application {

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
        filter.addAction(DownloadManagerInterface.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(new GlobalDownloadReceiver(), filter, null, new Handler(handlerThread.getLooper()));
    }

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
