package com.github.yeriomin.yalpstore;

import com.github.yeriomin.yalpstore.model.App;

import java.util.ArrayList;
import java.util.List;

class CategoryAppsTask extends GoogleApiAsyncTask {

    protected List<App> apps = new ArrayList<>();

    /**
     * params[0] is category id
     *
     */
    @Override
    protected Throwable doInBackground(String... params) {
        PlayStoreApiWrapper wrapper = new PlayStoreApiWrapper(context);
        try {
            CategoryAppsIterator iterator = wrapper.getCategoryAppsIterator(params[0]);
            if (!iterator.hasNext()) {
                return null;
            }
            for (App app: iterator.next()) {
                apps.add(app);
            }
        } catch (Throwable e) {
            return e;
        }
        return null;
    }
}
