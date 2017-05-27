package com.github.yeriomin.yalpstore;

import com.github.yeriomin.yalpstore.model.App;

import java.io.IOException;
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
        try {
            CategoryAppsIterator iterator = getIterator(params[0]);
            if (!iterator.hasNext()) {
                return null;
            }
            apps.addAll(iterator.next());
        } catch (Throwable e) {
            return e;
        }
        return null;
    }

    private CategoryAppsIterator getIterator(String categoryId) throws IOException {
        PlayStoreApiWrapper wrapper = new PlayStoreApiWrapper(context);
        CategoryAppsIterator iterator = wrapper.getCategoryAppsIterator(categoryId);
        iterator.setHideAppsWithAds(PreferenceActivity.getBoolean(context, PreferenceActivity.PREFERENCE_HIDE_APPS_WITH_ADS));
        iterator.setHideNonfreeApps(PreferenceActivity.getBoolean(context, PreferenceActivity.PREFERENCE_HIDE_NONFREE_APPS));
        return iterator;
    }
}
