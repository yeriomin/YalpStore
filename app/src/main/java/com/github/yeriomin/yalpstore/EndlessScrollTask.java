package com.github.yeriomin.yalpstore;

import com.github.yeriomin.playstoreapi.IteratorGooglePlayException;
import com.github.yeriomin.yalpstore.model.App;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

abstract public class EndlessScrollTask extends GoogleApiAsyncTask {

    protected AppListIterator iterator;
    protected List<App> apps = new ArrayList<>();

    abstract protected AppListIterator initIterator() throws IOException;

    public EndlessScrollTask(AppListIterator iterator) {
        this.iterator = iterator;
    }

    @Override
    protected Throwable doInBackground(String... params) {
        try {
            if (null == iterator) {
                iterator = initIterator();
                iterator.setHideAppsWithAds(PreferenceActivity.getBoolean(context, PreferenceActivity.PREFERENCE_HIDE_APPS_WITH_ADS));
                iterator.setHideNonfreeApps(PreferenceActivity.getBoolean(context, PreferenceActivity.PREFERENCE_HIDE_NONFREE_APPS));
            }
            if (!iterator.hasNext()) {
                return null;
            }
            while (iterator.hasNext() && apps.isEmpty()) {
                apps.addAll(getNextBatch(iterator));
            }
        } catch (IteratorGooglePlayException | IOException e) {
            return e;
        }
        return null;
    }

    protected List<App> getNextBatch(AppListIterator iterator) {
        return iterator.next();
    }

    @Override
    protected void onPostExecute(Throwable e) {
        super.onPostExecute(e);
        EndlessScrollActivity activity = (EndlessScrollActivity) context;
        if (null != e) {
            activity.clearApps();
            return;
        }
        activity.addApps(apps);
        activity.setIterator(iterator);
        apps.clear();
    }
}
