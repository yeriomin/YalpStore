package com.dragons.aurora.task.playstore;

import android.util.Log;

import com.dragons.aurora.AppListIterator;
import com.dragons.aurora.PlayStoreApiAuthenticator;
import com.dragons.aurora.model.App;
import com.dragons.aurora.model.Filter;
import com.dragons.aurora.playstoreapiv2.GooglePlayException;
import com.dragons.aurora.playstoreapiv2.IteratorGooglePlayException;
import com.dragons.aurora.task.AppProvidedCredentialsTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

abstract public class CategoryAppsTask extends ExceptionTask {

    protected Filter filter;
    protected AppListIterator iterator;

    public AppListIterator getIterator() {
        return iterator;
    }

    public void setIterator(AppListIterator iterator) {
        try {
            iterator.setGooglePlayApi(new PlayStoreApiAuthenticator(getContext()).getApi());
        } catch (IOException e) {
            Log.e(getClass().getSimpleName(), "Building an api object from preferences failed");
        }
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    protected List<App> getResult(AppListIterator iterator) throws IOException {

        setIterator(iterator);

        if (!iterator.hasNext()) {
            return new ArrayList<>();
        }

        List<App> apps = new ArrayList<>();
        while (iterator.hasNext() && apps.isEmpty()) {
            try {
                apps.addAll(getNextBatch(iterator));
            } catch (IteratorGooglePlayException e) {
                if (null == e.getCause()) {
                    continue;
                }
                if (noNetwork(e.getCause())) {
                    throw (IOException) e.getCause();
                } else if (e.getCause() instanceof GooglePlayException
                        && ((GooglePlayException) e.getCause()).getCode() == 401 && isDummy()) {
                    new AppProvidedCredentialsTask(getContext()).refreshToken();
                    iterator.setGooglePlayApi(new PlayStoreApiAuthenticator(getContext()).getApi());
                    apps.addAll(getNextBatch(iterator));
                }
            }
        }
        return apps;
    }

    protected List<App> getNextBatch(AppListIterator iterator) {
        return iterator.next();
    }
}
