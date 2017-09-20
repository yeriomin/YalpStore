package com.github.yeriomin.yalpstore.task.playstore;

import android.app.Activity;
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
    public CloneableTask clone() {
        SearchTask task = new SearchTask(iterator);
        task.setCategoryId(categoryId);
        task.setQuery(query);
        task.setErrorView(errorView);
        task.setContext(context);
        task.setProgressIndicator(progressIndicator);
        return null;
    }

    @Override
    protected AppListIterator initIterator() throws IOException {
        return new AppListIterator(new SearchIterator(new PlayStoreApiAuthenticator(context).getApi(), query));
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        categoryManager = new CategoryManager((Activity) context);
        installedPackageNames.addAll(getInstalledPackageNames(context));
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

    static private Set<String> getInstalledPackageNames(Context context) {
        if (installedPackageNames.isEmpty()) {
            for (PackageInfo reducedPackageInfo: context.getPackageManager().getInstalledPackages(0)) {
                installedPackageNames.add(reducedPackageInfo.packageName);
            }
        }
        return installedPackageNames;
    }
}
