package com.dragons.aurora.task.playstore;

import com.dragons.aurora.playstoreapiv2.GooglePlayAPI;
import com.dragons.aurora.playstoreapiv2.UrlIterator;

import java.io.IOException;

import com.dragons.aurora.AppListIterator;
import com.dragons.aurora.PlayStoreApiAuthenticator;

public class ClusterTask extends EndlessScrollTask implements CloneableTask {

    private String clusterUrl;

    public ClusterTask(AppListIterator iterator) {
        super(iterator);
    }

    public void setClusterUrl(String clusterUrl) {
        this.clusterUrl = clusterUrl;
    }

    @Override
    public CloneableTask clone() {
        ClusterTask task = new ClusterTask(iterator);
        task.setClusterUrl(clusterUrl);
        task.setFilter(filter);
        task.setErrorView(errorView);
        task.setContext(context);
        task.setProgressIndicator(progressIndicator);
        return task;
    }

    @Override
    protected AppListIterator initIterator() throws IOException {
        return new AppListIterator(new UrlIterator(new PlayStoreApiAuthenticator(context).getApi(), clusterUrl));
    }

    @Override
    public void setSubCategory(GooglePlayAPI.SUBCATEGORY subCategory) {

    }
}
