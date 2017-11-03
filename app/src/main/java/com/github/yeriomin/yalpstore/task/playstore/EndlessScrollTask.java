package com.github.yeriomin.yalpstore.task.playstore;

import android.util.Log;

import com.github.yeriomin.playstoreapi.GooglePlayAPI;
import com.github.yeriomin.yalpstore.AppListIterator;
import com.github.yeriomin.yalpstore.EndlessScrollActivity;
import com.github.yeriomin.yalpstore.PlayStoreApiAuthenticator;
import com.github.yeriomin.yalpstore.PreferenceActivity;
import com.github.yeriomin.yalpstore.model.App;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

abstract public class EndlessScrollTask extends PlayStorePayloadTask<List<App>> {

    protected AppListIterator iterator;
    protected List<App> apps = new ArrayList<>();

    abstract protected AppListIterator initIterator() throws IOException;

    public EndlessScrollTask(AppListIterator iterator) {
        this.iterator = iterator;
    }

    @Override
    protected List<App> getResult(GooglePlayAPI api, String... arguments) throws IOException {
        if (null == iterator) {
            iterator = initIterator();
            iterator.setHideAppsWithAds(PreferenceActivity.getBoolean(context, PreferenceActivity.PREFERENCE_HIDE_APPS_WITH_ADS));
            iterator.setHideNonfreeApps(PreferenceActivity.getBoolean(context, PreferenceActivity.PREFERENCE_HIDE_NONFREE_APPS));
        }
        try {
            iterator.setGooglePlayApi(new PlayStoreApiAuthenticator(context).getApi());
        } catch (IOException e) {
            Log.e(getClass().getName(), "Building an api object from preferences failed");
        }
        if (!iterator.hasNext()) {
            return apps;
        }
        while (iterator.hasNext() && apps.isEmpty()) {
            apps.addAll(getNextBatch(iterator));
        }
        return apps;
    }

    protected List<App> getNextBatch(AppListIterator iterator) {
        return iterator.next();
    }

    @Override
    protected void onPostExecute(List<App> apps) {
        super.onPostExecute(apps);
        EndlessScrollActivity activity = (EndlessScrollActivity) context;
        if (!success()) {
            activity.clearApps();
            return;
        }
        activity.addApps(apps);
        activity.setIterator(iterator);
        apps.clear();
    }
}
