package com.github.yeriomin.yalpstore.task.playstore;

import com.github.yeriomin.playstoreapi.UrlIterator;
import com.github.yeriomin.yalpstore.AppListIterator;
import com.github.yeriomin.yalpstore.PlayStoreApiAuthenticator;

import java.io.IOException;

public class ClusterTask extends EndlessScrollTask {

    private String clusterUrl;

    public ClusterTask(AppListIterator iterator) {
        super(iterator);
    }

    public void setClusterUrl(String clusterUrl) {
        this.clusterUrl = clusterUrl;
    }

    @Override
    protected AppListIterator initIterator() throws IOException {
        return new AppListIterator(new UrlIterator(new PlayStoreApiAuthenticator(context).getApi(), clusterUrl));
    }
}
