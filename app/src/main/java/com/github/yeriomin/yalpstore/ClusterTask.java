package com.github.yeriomin.yalpstore;

import com.github.yeriomin.playstoreapi.UrlIterator;
import com.github.yeriomin.yalpstore.model.App;

import java.util.ArrayList;
import java.util.List;

public class ClusterTask extends GoogleApiAsyncTask {

    protected ClusterIterator iterator;
    protected List<App> apps = new ArrayList<>();

    public ClusterTask(ClusterIterator iterator) {
        this.iterator = iterator;
    }

    @Override
    protected Throwable doInBackground(String... clusterUrl) {
        try {
            if (null == iterator) {
                iterator = new ClusterIterator(new UrlIterator(new PlayStoreApiAuthenticator(context).getApi(), clusterUrl[0]));
            }
            if (!iterator.hasNext()) {
                return null;
            }
            apps.addAll(iterator.next());
        } catch (Throwable e) {
            return e;
        }
        return null;
    }
}
