package com.github.yeriomin.yalpstore;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.github.yeriomin.yalpstore.model.App;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdatableAppsTask extends GoogleApiAsyncTask {

    protected List<App> updatableApps = new ArrayList<>();
    protected List<App> otherInstalledApps = new ArrayList<>();
    protected boolean explicitCheck;

    static public List<App> getInstalledApps(Context context) {
        List<App> apps = new ArrayList<>();

        PackageManager pm = context.getPackageManager();
        boolean showSystemApps = PreferenceActivity.getBoolean(context, PreferenceActivity.PREFERENCE_SHOW_SYSTEM_APPS);
        for (PackageInfo reducedPackageInfo: pm.getInstalledPackages(0)) {
            App app;
            try {
                app = new App(pm.getPackageInfo(reducedPackageInfo.packageName, PackageManager.GET_META_DATA | PackageManager.GET_PERMISSIONS));
            } catch (PackageManager.NameNotFoundException e) {
                continue;
            }
            if (!showSystemApps && app.isSystem()) {
                continue;
            }
            app.setDisplayName(pm.getApplicationLabel(app.getPackageInfo().applicationInfo).toString());
            app.setInstalled(true);
            apps.add(app);
        }
        return apps;
    }

    public void setExplicitCheck(boolean explicitCheck) {
        this.explicitCheck = explicitCheck;
    }

    private Map<String, App> getFilteredInstalledApps(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isBlacklist = prefs.getString(
            PreferenceActivity.PREFERENCE_UPDATE_LIST_WHITE_OR_BLACK,
            PreferenceActivity.LIST_BLACK
        ).equals(PreferenceActivity.LIST_BLACK);
        BlackWhiteListManager manager = new BlackWhiteListManager(context);

        Map<String, App> apps = new HashMap<>();
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
            apps.put(app.getPackageName(), app);
        }
        return apps;
    }

    @Override
    protected Throwable doInBackground(String... params) {
        // Building local apps list
        Map<String, App> installedApps = getFilteredInstalledApps(context);
        if (PreferenceActivity.getUpdateInterval(context) < 0 && !explicitCheck) {
            otherInstalledApps.addAll(installedApps.values());
            return null;
        }
        // Requesting info from Google Play Market for installed apps
        PlayStoreApiWrapper wrapper = new PlayStoreApiWrapper(this.context);
        List<App> appsFromPlayMarket = new ArrayList<>();
        try {
            appsFromPlayMarket.addAll(wrapper.getDetails(new ArrayList<>(installedApps.keySet())));
        } catch (Throwable e) {
            otherInstalledApps.addAll(installedApps.values());
            return e;
        }
        // Comparing versions and building updatable apps list
        for (App appFromMarket: appsFromPlayMarket) {
            String packageName = appFromMarket.getPackageName();
            if (TextUtils.isEmpty(packageName)) {
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
        otherInstalledApps.addAll(installedApps.values());
        return null;
    }

    @Override
    protected void onPostExecute(Throwable result) {
        super.onPostExecute(result);
        Collections.sort(updatableApps);
        Collections.sort(otherInstalledApps);
    }

    private App addInstalledAppInfo(App appFromMarket, App installedApp) {
        appFromMarket.setPackageInfo(installedApp.getPackageInfo());
        appFromMarket.setVersionName(installedApp.getVersionName());
        appFromMarket.setDisplayName(installedApp.getDisplayName());
        appFromMarket.setSystem(installedApp.isSystem());
        appFromMarket.setInstalled(true);
        return appFromMarket;
    }

    @Override
    protected void processIOException(IOException e) {
        super.processIOException(e);
        if (noNetwork(e) && context instanceof Activity) {
            toast(context, context.getString(R.string.error_no_network));
        }
    }
}
