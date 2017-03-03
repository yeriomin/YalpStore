package com.github.yeriomin.yalpstore;

import java.util.Iterator;

public abstract class AppListIterator implements Iterator {

    protected boolean hideNonfreeApps;
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

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }
}
