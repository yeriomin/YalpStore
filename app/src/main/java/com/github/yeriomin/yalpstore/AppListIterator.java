package com.github.yeriomin.yalpstore;

import com.github.yeriomin.yalpstore.model.App;

import java.util.Iterator;

public abstract class AppListIterator implements Iterator {

    protected boolean hideNonfreeApps;
    protected boolean hideAppsWithAds;
    protected String categoryId = CategoryManager.TOP;
    protected com.github.yeriomin.playstoreapi.AppListIterator iterator;

    public AppListIterator(com.github.yeriomin.playstoreapi.AppListIterator iterator) {
        this.iterator = iterator;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public void setHideNonfreeApps(boolean hideNonfreeApps) {
        this.hideNonfreeApps = hideNonfreeApps;
    }

    public void setHideAppsWithAds(boolean hideAppsWithAds) {
        this.hideAppsWithAds = hideAppsWithAds;
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    protected boolean shouldSkip(App app) {
        return (hideNonfreeApps && !app.isFree()) || (hideAppsWithAds && app.containsAds());
    }
}
