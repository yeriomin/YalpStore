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

import android.util.Log;

import com.dragons.aurora.playstoreapiv2.DocV2;
import com.dragons.aurora.playstoreapiv2.GooglePlayAPI;
import com.github.yeriomin.yalpstore.model.App;
import com.github.yeriomin.yalpstore.model.AppBuilder;
import com.github.yeriomin.yalpstore.model.Filter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AppListIterator implements Iterator {

    private Filter filter;
    protected com.dragons.aurora.playstoreapiv2.AppListIterator iterator;

    public AppListIterator(com.dragons.aurora.playstoreapiv2.AppListIterator iterator) {
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
        return app.isAd()
            || (!filter.isPaidApps() && !app.isFree())
            || (!filter.isAppsWithAds() && app.containsAds())
            || (!filter.isGsfDependentApps() && !app.getDependencies().isEmpty())
            || (filter.getRating() > 0 && app.getRating().getAverage() < filter.getRating())
            || (filter.getDownloads() > 0 && app.getInstalls() < filter.getDownloads())
        ;
    }

    protected void addApp(List<App> apps, App app) {
        if (null != filter && shouldSkip(app)) {
            Log.i(getClass().getSimpleName(), "Filtering out " + app.getPackageName());
        } else {
            apps.add(app);
        }
    }
}
