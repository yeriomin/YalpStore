package com.github.yeriomin.yalpstore;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.github.yeriomin.yalpstore.model.App;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class UpdatableAppsTask extends GoogleApiAsyncTask {

    static private List<App> appsFromPlayStore = new ArrayList<>();

    protected List<App> updatableApps = new ArrayList<>();
    protected Map<String, App> installedApps = new HashMap<>();
    protected boolean explicitCheck;

    static public Map<String, App> getInstalledApps(Context context) {
        Map<String, App> apps = new HashMap<>();
        PackageManager pm = context.getPackageManager();
        for (PackageInfo reducedPackageInfo: pm.getInstalledPackages(0)) {
            App app;
            try {
                app = new App(pm.getPackageInfo(reducedPackageInfo.packageName, PackageManager.GET_META_DATA | PackageManager.GET_PERMISSIONS));
            } catch (PackageManager.NameNotFoundException e) {
                continue;
            }
            app.setDisplayName(pm.getApplicationLabel(app.getPackageInfo().applicationInfo).toString());
            apps.put(app.getPackageName(), app);
        }
        return apps;
    }

    static public Map<String, App> filterSystemApps(Map<String, App> apps) {
        Map<String, App> result = new HashMap<>();
        for (App app: apps.values()) {
            if (!app.isSystem()) {
                result.put(app.getPackageName(), app);
            }
        }
        return result;
    }

    static public Map<String, App> filterBlacklistedApps(Context context, Map<String, App> apps) {
        Set<String> packageNames = new HashSet<>(apps.keySet());
        if (PreferenceManager.getDefaultSharedPreferences(context).getString(PreferenceActivity.PREFERENCE_UPDATE_LIST_WHITE_OR_BLACK, PreferenceActivity.LIST_BLACK).equals(PreferenceActivity.LIST_BLACK)) {
            packageNames.removeAll(new BlackWhiteListManager(context).get());
        } else {
            packageNames.retainAll(new BlackWhiteListManager(context).get());
        }
        Map<String, App> result = new HashMap<>();
        for (App app: apps.values()) {
            if (packageNames.contains(app.getPackageName())) {
                result.put(app.getPackageName(), app);
            }
        }
        return result;
    }

    public void setExplicitCheck(boolean explicitCheck) {
        this.explicitCheck = explicitCheck;
    }

    @Override
    protected Throwable doInBackground(String... params) {
        // Building local apps list
        installedApps = getInstalledApps(context);
        if (!PreferenceActivity.getBoolean(context, PreferenceActivity.PREFERENCE_SHOW_SYSTEM_APPS)) {
            installedApps = filterSystemApps(installedApps);
        }
        // Requesting info from Google Play Market for installed apps
        List<App> appsFromPlayStore = new ArrayList<>();
        try {
            appsFromPlayStore.addAll(getAppsFromPlayStore(filterBlacklistedApps(context, installedApps).keySet()));
        } catch (Throwable e) {
            return e;
        }
        // Comparing versions and building updatable apps list
        for (App appFromMarket: appsFromPlayStore) {
            String packageName = appFromMarket.getPackageName();
            if (TextUtils.isEmpty(packageName) || !installedApps.containsKey(packageName)) {
                continue;
            }
            App installedApp = installedApps.get(packageName);
            appFromMarket = addInstalledAppInfo(appFromMarket, installedApp);
            if (installedApp.getVersionCode() < appFromMarket.getVersionCode()) {
                installedApps.remove(packageName);
                updatableApps.add(appFromMarket);
            } else {
                installedApps.put(packageName, appFromMarket);
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Throwable result) {
        super.onPostExecute(result);
        Collections.sort(updatableApps);
    }

    @Override
    protected void processIOException(IOException e) {
        super.processIOException(e);
        if (noNetwork(e) && context instanceof Activity) {
            toast(context, context.getString(R.string.error_no_network));
        }
    }

    private App addInstalledAppInfo(App appFromMarket, App installedApp) {
        if (null != installedApp) {
            appFromMarket.setPackageInfo(installedApp.getPackageInfo());
            appFromMarket.setVersionName(installedApp.getVersionName());
            appFromMarket.setDisplayName(installedApp.getDisplayName());
            appFromMarket.setSystem(installedApp.isSystem());
            appFromMarket.setInstalled(true);
        }
        return appFromMarket;
    }

    private List<App> getAppsFromPlayStore(Collection<String> packageNames) throws IOException {
        if (explicitCheck) {
            appsFromPlayStore = new PlayStoreApiWrapper(this.context).getDetails(new ArrayList<>(packageNames));
        }
        return appsFromPlayStore;
    }
}
