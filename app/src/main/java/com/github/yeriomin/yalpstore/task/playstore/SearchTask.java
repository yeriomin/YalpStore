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

package com.github.yeriomin.yalpstore.task.playstore;

import android.content.Context;
import android.content.pm.PackageInfo;

import com.github.yeriomin.playstoreapi.SearchIterator;
import com.github.yeriomin.yalpstore.AppListIterator;
import com.github.yeriomin.yalpstore.CategoryManager;
import com.github.yeriomin.yalpstore.PlayStoreApiAuthenticator;
import com.github.yeriomin.yalpstore.model.App;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SearchTask extends EndlessScrollTask implements CloneableTask {

    static private Set<String> installedPackageNames = new HashSet<>();

    private CategoryManager categoryManager;
    private String query;

    public SearchTask(AppListIterator iterator) {
        super(iterator);
    }

    public void setQuery(String query) {
        this.query = query;
    }

    @Override
    public CloneableTask clone() {
        SearchTask task = new SearchTask(iterator);
        task.setFilter(filter);
        task.setQuery(query);
        task.setErrorView(errorView);
        task.setContext(context);
        task.setProgressIndicator(progressIndicator);
        return task;
    }

    @Override
    protected AppListIterator initIterator() throws IOException {
        return new AppListIterator(new SearchIterator(new PlayStoreApiAuthenticator(context).getApi(), query));
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        categoryManager = new CategoryManager(context);
        if (installedPackageNames.isEmpty()) {
            installedPackageNames = getInstalledPackageNames(context);
        }
    }

    @Override
    protected void onPostExecute(List<App> apps) {
        super.onPostExecute(apps);
        if (success()) {
            BackgroundCategoryTask task = new BackgroundCategoryTask();
            task.setContext(context);
            task.setManager(categoryManager);
            task.execute();

            WishlistUpdateTask wishlistUpdateTask = new WishlistUpdateTask();
            wishlistUpdateTask.setContext(context);
            wishlistUpdateTask.execute();
        }
    }

    @Override
    protected List<App> getNextBatch(AppListIterator iterator) {
        List<App> apps = new ArrayList<>();
        for (App app: iterator.next()) {
            app.setInstalled(installedPackageNames.contains(app.getPackageName()));
            if (categoryManager.fits(app.getCategoryId(), filter.getCategory())) {
                apps.add(app);
            }
        }
        return apps;
    }

    static private Set<String> getInstalledPackageNames(Context context) {
        Set<String> newList = new HashSet<>();
        try {
            for (PackageInfo reducedPackageInfo : context.getPackageManager().getInstalledPackages(0)) {
                newList.add(reducedPackageInfo.packageName);
            }
        } catch (RuntimeException e) {
            // TransactionTooLargeException might happen if the user has too many apps
            // Marking apps as installed in search is not very important, so lets ignore this
        }
        return newList;
    }
}
