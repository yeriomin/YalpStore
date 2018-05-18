package com.dragons.aurora.task.playstore;

import android.text.TextUtils;

import com.dragons.aurora.model.App;
import com.dragons.aurora.playstoreapiv2.GooglePlayAPI;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class InstalledAppsTaskHelper extends UpdatableAppsTaskHelper {

    protected List<App> getInstalledApps(GooglePlayAPI api, boolean removeSystem) throws IOException {
        api.toc();
        List<App> allMarketApps = new ArrayList<>();
        allMarketApps.clear();
        Map<String, App> installedApps = getInstalledApps();
        if (removeSystem)
            installedApps = filterSystemApps(installedApps);
        for (App appFromMarket : getAppsFromPlayStore(api, installedApps.keySet())) {
            String packageName = appFromMarket.getPackageName();
            if (TextUtils.isEmpty(packageName) || !installedApps.containsKey(packageName)) {
                continue;
            }
            App installedApp = installedApps.get(packageName);
            appFromMarket = addInstalledAppInfo(appFromMarket, installedApp);
            allMarketApps.add(appFromMarket);
        }
        return allMarketApps;
    }
}