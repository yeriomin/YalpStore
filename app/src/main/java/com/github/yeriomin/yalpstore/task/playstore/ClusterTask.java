package com.github.yeriomin.yalpstore.task.playstore;

import com.github.yeriomin.playstoreapi.UrlIterator;
import com.github.yeriomin.yalpstore.AppListIterator;
import com.github.yeriomin.yalpstore.PlayStoreApiAuthenticator;

import java.io.IOException;

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
}
