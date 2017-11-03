package com.github.yeriomin.yalpstore;

import android.util.Log;

import com.github.yeriomin.playstoreapi.DocV2;
import com.github.yeriomin.playstoreapi.GooglePlayAPI;
import com.github.yeriomin.yalpstore.model.App;
import com.github.yeriomin.yalpstore.model.AppBuilder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AppListIterator implements Iterator {

    protected boolean hideNonfreeApps;
    protected boolean hideAppsWithAds;
    protected com.github.yeriomin.playstoreapi.AppListIterator iterator;

    public AppListIterator(com.github.yeriomin.playstoreapi.AppListIterator iterator) {
        this.iterator = iterator;
    }

    public void setGooglePlayApi(GooglePlayAPI googlePlayApi) {
        iterator.setGooglePlayApi(googlePlayApi);
    }

    public void setHideNonfreeApps(boolean hideNonfreeApps) {
        this.hideNonfreeApps = hideNonfreeApps;
    }

    public void setHideAppsWithAds(boolean hideAppsWithAds) {
        this.hideAppsWithAds = hideAppsWithAds;
    }

    @Override
    public List<App> next() {
        List<App> apps = new ArrayList<>();
        for (DocV2 details: iterator.next()) {
            addApp(apps, AppBuilder.build(details));
        }
        return apps;
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    protected boolean shouldSkip(App app) {
        return (hideNonfreeApps && !app.isFree()) || (hideAppsWithAds && app.containsAds());
    }

    protected void addApp(List<App> apps, App app) {
        if (shouldSkip(app)) {
            Log.i(getClass().getName(), "Skipping non-free/ad-containing app " + app.getPackageName());
        } else {
            apps.add(app);
        }
    }
}
