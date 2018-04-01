package in.dragons.galaxy.task.playstore;

import android.util.Log;

import com.github.yeriomin.playstoreapi.GooglePlayAPI;
import com.github.yeriomin.playstoreapi.GooglePlayException;
import com.github.yeriomin.playstoreapi.IteratorGooglePlayException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import in.dragons.galaxy.AppListIterator;
import in.dragons.galaxy.PlayStoreApiAuthenticator;
import in.dragons.galaxy.R;
import in.dragons.galaxy.activities.EndlessScrollActivity;
import in.dragons.galaxy.fragment.PreferenceFragment;
import in.dragons.galaxy.model.App;
import in.dragons.galaxy.model.Filter;

abstract public class EndlessScrollTask extends PlayStorePayloadTask<List<App>> {

    protected Filter filter;
    protected AppListIterator iterator;

    abstract protected AppListIterator initIterator() throws IOException;

    abstract public void setSubCategory(GooglePlayAPI.SUBCATEGORY subCategory);

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    EndlessScrollTask(AppListIterator iterator) {
        this.iterator = iterator;
    }

    @Override
    protected List<App> getResult(GooglePlayAPI api, String... arguments) throws IOException {
        if (null == iterator) {
            iterator = initIterator();
            iterator.setFilter(filter);
        }
        try {
            iterator.setGooglePlayApi(new PlayStoreApiAuthenticator(context).getApi());
        } catch (IOException e) {
            Log.e(getClass().getSimpleName(), "Building an api object from preferences failed");
        }
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
                        && ((GooglePlayException) e.getCause()).getCode() == 401
                        && PreferenceFragment.getBoolean(context, PlayStoreApiAuthenticator.PREFERENCE_APP_PROVIDED_EMAIL)
                        ) {
                    PlayStoreApiAuthenticator authenticator = new PlayStoreApiAuthenticator(context);
                    authenticator.refreshToken();
                    iterator.setGooglePlayApi(authenticator.getApi());
                    apps.addAll(getNextBatch(iterator));
                }
            }
        }
        return apps;
    }

    protected List<App> getNextBatch(AppListIterator iterator) {
        return iterator.next();
    }

    @Override
    protected void onPostExecute(List<App> apps) {
        EndlessScrollActivity activity = (EndlessScrollActivity) context;
        if (null == apps) {
            apps = new ArrayList<>();
        }
        if (!success() && !activity.getListView().getAdapter().isEmpty()) {
            errorView = null;
        }
        super.onPostExecute(apps);
        activity.addApps(apps);
        activity.setIterator(iterator);
        if (null != errorView && success() && apps.isEmpty()) {
            errorView.setText(context.getString(R.string.list_empty_search));
        }
    }
}
