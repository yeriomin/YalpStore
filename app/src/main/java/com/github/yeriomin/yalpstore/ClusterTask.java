package com.github.yeriomin.yalpstore;

import com.github.yeriomin.playstoreapi.UrlIterator;

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
        return new ClusterIterator(new UrlIterator(new PlayStoreApiAuthenticator(context).getApi(), clusterUrl));
    }
}
