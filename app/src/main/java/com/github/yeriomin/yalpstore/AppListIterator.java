package com.github.yeriomin.yalpstore;

import android.util.Log;

import com.github.yeriomin.playstoreapi.DocV2;
import com.github.yeriomin.playstoreapi.GooglePlayAPI;
import com.github.yeriomin.yalpstore.model.App;
import com.github.yeriomin.yalpstore.model.AppBuilder;
import com.github.yeriomin.yalpstore.model.Filter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AppListIterator implements Iterator {

    protected Filter filter = new Filter();
    protected com.github.yeriomin.playstoreapi.AppListIterator iterator;

    public AppListIterator(com.github.yeriomin.playstoreapi.AppListIterator iterator) {
        this.iterator = iterator;
    }

    public void setGooglePlayApi(GooglePlayAPI googlePlayApi) {
        iterator.setGooglePlayApi(googlePlayApi);
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
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
        return (!filter.isPaidApps() && !app.isFree())
            || (!filter.isAppsWithAds() && app.containsAds())
            || (!filter.isGsfDependentApps() && !app.getDependencies().isEmpty())
            || (filter.getRating() > 0 && app.getRating().getAverage() < filter.getRating())
            || (filter.getDownloads() > 0 && app.getInstalls() < filter.getDownloads())
        ;
    }

    protected void addApp(List<App> apps, App app) {
        if (shouldSkip(app)) {
            Log.i(getClass().getSimpleName(), "Filtering out " + app.getPackageName());
        } else {
            apps.add(app);
        }
    }
}
