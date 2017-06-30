package com.github.yeriomin.yalpstore;

import android.app.Activity;

import com.github.yeriomin.yalpstore.model.App;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class SearchTask extends EndlessScrollTask {

    private Set<String> installedPackageNames = new HashSet<>();
    private CategoryManager categoryManager;
    private String query;
    private String categoryId;

    public SearchTask(AppListIterator iterator) {
        super(iterator);
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    @Override
    protected SearchIterator initIterator() throws IOException {
        return new SearchIterator(new com.github.yeriomin.playstoreapi.SearchIterator(new PlayStoreApiAuthenticator(context).getApi(), query));
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        categoryManager = new CategoryManager((Activity) context);
        installedPackageNames.addAll(UpdatableAppsTask.getInstalledApps(context).keySet());
    }

    @Override
    protected List<App> getNextBatch(AppListIterator iterator) {
        List<App> apps = new ArrayList<>();
        for (App app: iterator.next()) {
            app.setInstalled(installedPackageNames.contains(app.getPackageName()));
            if (categoryManager.fits(app.getCategoryId(), categoryId)) {
                apps.add(app);
            }
        }
        return apps;
    }
}
