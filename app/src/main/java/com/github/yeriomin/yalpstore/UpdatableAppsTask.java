package com.github.yeriomin.yalpstore;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.github.yeriomin.yalpstore.model.App;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class UpdatableAppsTask extends GoogleApiAsyncTask {

    protected List<App> updatableApps = new ArrayList<>();
    protected List<App> otherInstalledApps = new ArrayList<>();

    static public List<App> getInstalledApps(Context context) {
        List<App> apps = new ArrayList<>();

        PackageManager pm = context.getPackageManager();
        boolean showSystemApps = PreferenceActivity.getBoolean(context, PreferenceActivity.PREFERENCE_SHOW_SYSTEM_APPS);
        List<PackageInfo> packages = pm.getInstalledPackages(PackageManager.GET_META_DATA | PackageManager.GET_PERMISSIONS);
        for (PackageInfo packageInfo : packages) {
            App app = new App(packageInfo);
            if (!showSystemApps && app.isSystem()) {
                continue;
            }
            app.setDisplayName(pm.getApplicationLabel(packageInfo.applicationInfo).toString());
            try {
                app.setIcon(pm.getApplicationIcon(packageInfo.applicationInfo));
            } catch (OutOfMemoryError e) {
                Log.e(UpdatableAppsTask.class.getName(), "OutOfMemoryError occurred while trying to get icon for " + packageInfo.packageName);
            }
            app.setInstalled(true);
            apps.add(app);
        }
        return apps;
    }

    private List<App> getFilteredInstalledApps(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isBlacklist = prefs.getString(
            PreferenceActivity.PREFERENCE_UPDATE_LIST_WHITE_OR_BLACK,
            PreferenceActivity.LIST_BLACK
        ).equals(PreferenceActivity.LIST_BLACK);
        BlackWhiteListManager manager = new BlackWhiteListManager(context);

        List<App> apps = new ArrayList<>();
        for (App app: getInstalledApps(context)) {
            boolean inList = manager.contains(app.getPackageName());
            if ((isBlacklist && inList) || (!isBlacklist && !inList)) {
                Log.i(
                    UpdatableAppsActivity.class.getName(),
                    "Ignoring updates for " + app.getPackageName()
                        + " isBlacklist=" + isBlacklist
                        + " inList=" + inList
                );
                continue;
            }
            apps.add(app);
        }
        return apps;
    }

    @Override
    protected Throwable doInBackground(String... params) {
        // Building local apps list
        List<String> installedAppIds = new ArrayList<>();
        Map<String, App> appMap = new HashMap<>();
        for (App installedApp: getFilteredInstalledApps(context)) {
            String packageName = installedApp.getPackageInfo().packageName;
            installedAppIds.add(packageName);
            appMap.put(packageName, installedApp);
        }
        // Requesting info from Google Play Market for installed apps
        PlayStoreApiWrapper wrapper = new PlayStoreApiWrapper(this.context);
        List<App> appsFromPlayMarket = new ArrayList<>();
        try {
            appsFromPlayMarket.addAll(wrapper.getDetails(installedAppIds));
        } catch (Throwable e) {
            otherInstalledApps.addAll(appMap.values());
            return e;
        }
        // Comparing versions and building updatable apps list
        for (App appFromMarket : appsFromPlayMarket) {
            String packageName = appFromMarket.getPackageName();
            if (TextUtils.isEmpty(packageName)) {
                continue;
            }
            App installedApp = appMap.get(packageName);
            appFromMarket.setPackageInfo(installedApp.getPackageInfo());
            appFromMarket.setDisplayName(installedApp.getDisplayName());
            appFromMarket.setIcon(installedApp.getIcon());
            appFromMarket.setSystem(installedApp.isSystem());
            if (installedApp.getVersionCode() < appFromMarket.getVersionCode()) {
                appMap.remove(packageName);
                updatableApps.add(appFromMarket);
            } else {
                appMap.put(packageName, appFromMarket);
            }
        }
        otherInstalledApps.addAll(appMap.values());
        return null;
    }
}
