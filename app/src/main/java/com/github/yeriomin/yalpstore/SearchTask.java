package com.github.yeriomin.yalpstore;

import com.github.yeriomin.yalpstore.model.App;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class SearchTask extends GoogleApiAsyncTask {

    protected List<App> apps = new ArrayList<>();
    private Set<String> installedPackageNames = new HashSet<>();

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        List<App> installed = UpdatableAppsTask.getInstalledApps(context);
        for (App installedApp : installed) {
            installedPackageNames.add(installedApp.getPackageName());
        }
    }

    @Override
    protected Throwable doInBackground(String... params) {
        PlayStoreApiWrapper wrapper = new PlayStoreApiWrapper(context);
        try {
            AppSearchResultIterator iterator = wrapper.getSearchIterator(params[0]);
            if (iterator.hasNext()) {
                apps.addAll(iterator.next());
            }
            for (App app : apps) {
                app.setInstalled(installedPackageNames.contains(app.getPackageName()));
            }
        } catch (Throwable e) {
            return e;
        }
        return null;
    }
}
