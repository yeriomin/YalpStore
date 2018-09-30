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

import android.app.Application;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.net.http.HttpResponseCache;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.preference.PreferenceManager;
import android.util.Log;

import com.github.yeriomin.yalpstore.model.App;
import com.github.yeriomin.yalpstore.model.LoginInfo;
import com.github.yeriomin.yalpstore.model.LoginInfoDao;
import com.github.yeriomin.yalpstore.notification.CancelDownloadReceiver;
import com.github.yeriomin.yalpstore.notification.IgnoreUpdatesReceiver;
import com.github.yeriomin.yalpstore.notification.SignatureCheckReceiver;
import com.github.yeriomin.yalpstore.task.FdroidListTask;
import com.github.yeriomin.yalpstore.task.InstalledAppsTask;
import com.github.yeriomin.yalpstore.task.OldApkCleanupTask;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import info.guardianproject.netcipher.NetCipher;
import info.guardianproject.netcipher.proxy.OrbotHelper;

import static android.net.ConnectivityManager.CONNECTIVITY_ACTION;
import static com.github.yeriomin.yalpstore.InstalledAppsActivity.INSTALLED_APPS_LOADED_ACTION;
import static com.github.yeriomin.yalpstore.PreferenceUtil.PREFERENCE_DOWNLOAD_INTERNAL_STORAGE;
import static com.github.yeriomin.yalpstore.PreferenceUtil.PREFERENCE_USE_TOR;

public class YalpStoreApplication extends Application {

    public static final Map<String, App> installedPackages = new ConcurrentHashMap<>();
    public static final Set<String> fdroidPackageNames = new HashSet<>();
    public static final SharedPreferencesCachedSet wishlist = new SharedPreferencesCachedSet("wishlist");

    public static LoginInfo user = new LoginInfo();

    private static final AtomicInteger runningActivities = new AtomicInteger(0);

    private boolean isBackgroundUpdating = false;
    private List<String> pendingUpdates = new ArrayList<>();
    private ProxyOnChangeListener listener;

    public static boolean isForeground() {
        return runningActivities.get() > 0;
    }

    public static void incrementActivityCount() {
        runningActivities.incrementAndGet();
    }

    public static void decrementActivityCount() {
        runningActivities.decrementAndGet();
    }

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
        if (!BuildConfig.DEBUG && Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            try {
                HttpResponseCache.install(new File(getCacheDir(), "http"), 5 * 1024 * 1024);
            } catch (IOException e) {
                Log.e(getClass().getSimpleName(), "Could not register cache " + e.getMessage());
            }
        }
        PreferenceUtil.prefillInstallationMethod(this);
        PreferenceManager.setDefaultValues(this, R.xml.settings, false);
        initNetcipher();
        initUser();
        Thread.setDefaultUncaughtExceptionHandler(new YalpStoreUncaughtExceptionHandler(getApplicationContext()));
        registerInstallReceiver();
        registerConnectivityReceiver();
        registerSecondaryDownloadReceivers();
        try {
            new FdroidListTask(this.getApplicationContext()).executeOnExecutorIfPossible();
        } catch (Throwable e) {
            // It does not matter if this fails, f-droid links are convenient but not essential
            Log.e(getClass().getSimpleName(), "Could not get F-Droid app list: " + e.getClass().getName() + " " + e.getMessage());
        }
        InitializingInstalledAppsTask installedAppsTask = new InitializingInstalledAppsTask();
        installedAppsTask.setContext(this.getApplicationContext());
        installedAppsTask.executeOnExecutorIfPossible();
        wishlist.setPreferences(PreferenceUtil.getDefaultSharedPreferences(this));
        if (PreferenceUtil.getBoolean(this, PREFERENCE_DOWNLOAD_INTERNAL_STORAGE)) {
            OldApkCleanupTask task = new OldApkCleanupTask(this);
            task.setDeleteAll(true);
            task.executeOnExecutorIfPossible();
        }
    }

    private void registerInstallReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addDataScheme("package");
        filter.addAction(Intent.ACTION_INSTALL_PACKAGE);
        filter.addAction(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        filter.addAction(
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH
                ? Intent.ACTION_PACKAGE_FULLY_REMOVED
                : Intent.ACTION_PACKAGE_REMOVED
        );
        filter.addAction(GlobalInstallReceiver.ACTION_PACKAGE_INSTALLATION_FAILED);
        registerReceiver(new GlobalInstallReceiver(), filter);
    }

    private void registerConnectivityReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(CONNECTIVITY_ACTION);
        registerReceiver(new ConnectivityChangeReceiver(), filter);
    }

    private void registerSecondaryDownloadReceivers() {
        registerReceiver(new CancelDownloadReceiver(), new IntentFilter(CancelDownloadReceiver.ACTION_CANCEL_DOWNLOAD));
        registerReceiver(new SignatureCheckReceiver(), new IntentFilter(SignatureCheckReceiver.ACTION_CHECK_APK));
        registerReceiver(new IgnoreUpdatesReceiver(), new IntentFilter(IgnoreUpdatesReceiver.ACTION_IGNORE_UPDATES));
    }

    private void initUser() {
        int id = PreferenceUtil.getDefaultSharedPreferences(this).getInt(PlayStoreApiAuthenticator.PREFERENCE_USER_ID, 0);
        Log.i(getClass().getSimpleName(), "Current user id is " + id);
        if (id != 0) {
            SQLiteDatabase db = new SqliteHelper(this).getReadableDatabase();
            LoginInfo loginInfo = new LoginInfoDao(db).get(id);
            db.close();
            if (null != loginInfo) {
                user = loginInfo;
            }
        }
    }

    public void initNetcipher() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
            return;
        }
        listener = new ProxyOnChangeListener(this);
        PreferenceUtil.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(listener);
        Proxy proxy = PreferenceUtil.getProxy(this);
        if (PreferenceUtil.getBoolean(this, PREFERENCE_USE_TOR)) {
            OrbotHelper.requestStartTor(this);
            NetCipher.useTor();
        } else if (null != proxy) {
            NetCipher.setProxy(proxy);
        } else {
            NetCipher.clearProxy();
        }
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

    private static class ProxyOnChangeListener implements SharedPreferences.OnSharedPreferenceChangeListener {

        YalpStoreApplication application;

        public ProxyOnChangeListener(YalpStoreApplication application) {
            this.application = application;
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(PreferenceUtil.PREFERENCE_ENABLE_PROXY)
                || key.equals(PreferenceUtil.PREFERENCE_PROXY_TYPE)
                || key.equals(PreferenceUtil.PREFERENCE_PROXY_HOST)
                || key.equals(PreferenceUtil.PREFERENCE_PROXY_PORT)
                || key.equals(PreferenceUtil.PREFERENCE_USE_TOR)
            ) {
                try {
                    application.initNetcipher();
                } catch (RuntimeException e) {
                    ContextUtil.toastLong(application, e.getMessage());
                }
            }
        }
    }

    private static class InitializingInstalledAppsTask extends InstalledAppsTask {

        public InitializingInstalledAppsTask() {
            setIncludeSystemApps(true);
        }

        @Override
        protected void onPostExecute(Map<String, App> apps) {
            super.onPostExecute(apps);
            apps.get(BuildConfig.APPLICATION_ID).setFree(true);
            YalpStoreApplication.installedPackages.clear();
            YalpStoreApplication.installedPackages.putAll(apps);
            context.sendBroadcast(new Intent(INSTALLED_APPS_LOADED_ACTION));
        }
    }
}
