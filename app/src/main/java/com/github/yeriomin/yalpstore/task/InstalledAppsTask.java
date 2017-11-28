package com.github.yeriomin.yalpstore.task;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.github.yeriomin.yalpstore.model.App;

import java.util.HashMap;
import java.util.Map;

public class InstalledAppsTask extends TaskWithProgress<Map<String, App>> {

    protected boolean includeSystemApps = false;

    public void setIncludeSystemApps(boolean includeSystemApps) {
        this.includeSystemApps = includeSystemApps;
    }

    static public App getInstalledApp(PackageManager pm, String packageName) {
        try {
            App app = new App(pm.getPackageInfo(packageName, PackageManager.GET_META_DATA | PackageManager.GET_PERMISSIONS));
            app.setDisplayName(pm.getApplicationLabel(app.getPackageInfo().applicationInfo).toString());
            return app;
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    static protected Map<String, App> filterSystemApps(Map<String, App> apps) {
        Map<String, App> result = new HashMap<>();
        for (App app: apps.values()) {
            if (!app.isSystem()) {
                result.put(app.getPackageName(), app);
            }
        }
        return result;
    }

    public Map<String, App> getInstalledApps() {
        Map<String, App> installedApps = new HashMap<>();
        PackageManager pm = context.getPackageManager();
        for (PackageInfo reducedPackageInfo: pm.getInstalledPackages(0)) {
            App app = getInstalledApp(pm, reducedPackageInfo.packageName);
            if (null != app) {
                installedApps.put(app.getPackageName(), app);
            }
        }
        if (!includeSystemApps) {
            installedApps = filterSystemApps(installedApps);
        }
        return installedApps;
    }

    @Override
    protected Map<String, App> doInBackground(String... strings) {
        return getInstalledApps(true);
    }
}
