package com.github.yeriomin.yalpstore;

import android.util.Log;

import com.github.yeriomin.playstoreapi.DocV2;
import com.github.yeriomin.playstoreapi.GooglePlayAPI;
import com.github.yeriomin.playstoreapi.SearchResponse;
import com.github.yeriomin.yalpstore.model.App;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class AppSearchResultIterator implements Iterator<List<App>> {

    private GooglePlayAPI.SearchIterator iterator;
    private boolean hideNonfreeApps;

    public AppSearchResultIterator(GooglePlayAPI.SearchIterator iterator) {
        this.iterator = iterator;
    }

    public String getQuery() {
        return this.iterator.getQuery();
    }

    public void setHideNonfreeApps(boolean hideNonfreeApps) {
        this.hideNonfreeApps = hideNonfreeApps;
    }

    @Override
    public List<App> next() {
        List<App> apps = new ArrayList<>();
        SearchResponse response = iterator.next();
        if (response.getDocCount() > 0) {
            for (DocV2 details : response.getDocList().get(0).getChildList()) {
                App app = PlayStoreApiWrapper.buildApp(details);
                if (hideNonfreeApps && !app.isFree()) {
                    Log.i(this.getClass().getName(), "Skipping non-free app " + app.getPackageName());
                } else {
                    apps.add(app);
                }
            }
        }
        return apps;
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }
}
