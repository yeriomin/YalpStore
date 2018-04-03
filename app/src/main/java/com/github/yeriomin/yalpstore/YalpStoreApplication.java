/*
 * Yalp Store
 * Copyright (C) 2018 Sergey Yeriomin <yeriomin@gmail.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.github.yeriomin.yalpstore;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.http.HttpResponseCache;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.File;
import java.io.IOException;
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
        removePendingUpdate(packageName, false);
    }

    public void removePendingUpdate(String packageName, boolean installed) {
        pendingUpdates.remove(packageName);
        Intent appIntent = new Intent(UpdateAllReceiver.ACTION_APP_UPDATE_COMPLETE);
        appIntent.putExtra(UpdateAllReceiver.EXTRA_PACKAGE_NAME, packageName);
        appIntent.putExtra(UpdateAllReceiver.EXTRA_UPDATE_ACTUALLY_INSTALLED, installed);
        sendBroadcast(appIntent, null);
        if (pendingUpdates.isEmpty()) {
            isBackgroundUpdating = false;
            Intent allIntent = new Intent(UpdateAllReceiver.ACTION_ALL_UPDATES_COMPLETE);
            sendBroadcast(allIntent, null);
        }
    }

    public void clearPendingUpdates() {
        pendingUpdates.clear();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            try {
                HttpResponseCache.install(new File(getCacheDir(), "http"), 5 * 1024 * 1024);
            } catch (IOException e) {
                Log.e(getClass().getSimpleName(), "Could not register cache " + e.getMessage());
            }
        }
        PreferenceUtil.prefillInstallationMethod(this);
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

    public boolean isTv() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) {
            return false;
        }
        int uiMode = getResources().getConfiguration().uiMode;
        return (uiMode & Configuration.UI_MODE_TYPE_MASK) == Configuration.UI_MODE_TYPE_TELEVISION
            || getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEVISION)
            || getPackageManager().hasSystemFeature(PackageManager.FEATURE_LEANBACK)
        ;
    }
}
