package in.dragons.galaxy.activities;

import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.widget.ListView;

import com.percolate.caffeine.PhoneUtils;
import com.percolate.caffeine.ToastUtils;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import in.dragons.galaxy.PlayStoreApiAuthenticator;
import in.dragons.galaxy.adapters.AppListAdapter;
import in.dragons.galaxy.fragment.PreferenceFragment;
import in.dragons.galaxy.model.App;
import in.dragons.galaxy.view.AppBadge;
import in.dragons.galaxy.view.ListItem;

public abstract class BaseActivity extends RxAppCompatActivity {

    static protected boolean logout = false;
    static protected boolean firstLogin = true;

    abstract protected ListItem getListItem(App app);

    abstract protected void redrawDetails(App app);

    abstract public void loadInstalledApps();

    protected ListView listView;
    protected Map<String, ListItem> listItems = new HashMap<>();

    public static void cascadeFinish() {
        BaseActivity.logout = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        logout = false;
    }

    protected boolean isConnected() {
        return PhoneUtils.isNetworkAvailable(this);
    }

    protected boolean isLoggedIn() {
        return PreferenceFragment.getBoolean(this, "LOGGED_IN");
    }

    protected boolean isDummy() {
        return PreferenceFragment.getBoolean(this, "DUMMY_ACC");
    }

    protected boolean isGoogle() {
        return PreferenceFragment.getBoolean(this, "GOOGLE_ACC");
    }

    protected void notifyConnected(final Context context) {
        if (!isConnected())
            ToastUtils.quickToast(this, "No network").show();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    protected void addQueryTextListener(SearchView searchView) {
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        if (null != searchManager) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setLayoutParams(new Toolbar.LayoutParams(Gravity.RIGHT));
        }
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextChange(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.setQuery("", false);
                searchView.setIconified(true);
                searchView.clearFocus();
                setQuery(query);
                return false;
            }
        });

        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return true;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                Cursor cursor = searchView.getSuggestionsAdapter().getCursor();
                cursor.moveToPosition(position);
                String suggestion = cursor.getString(2);
                searchView.setQuery(suggestion, true);
                return false;
            }
        });
    }

    protected void setQuery(String query) {
        Intent i = new Intent(BaseActivity.this, SearchActivity.class);
        i.setAction(Intent.ACTION_SEARCH);
        i.putExtra(SearchManager.QUERY, query);
        startActivity(i);
    }

    protected App getAppByListPosition(int position) {
        ListItem listItem = (ListItem) getListView().getItemAtPosition(position);
        if (null == listItem || !(listItem instanceof AppBadge)) {
            return null;
        }
        return ((AppBadge) listItem).getApp();
    }

    public void addApps(List<App> appsToAdd) {
        addApps(appsToAdd, true);
    }

    public void addApps(List<App> appsToAdd, boolean update) {
        AppListAdapter adapter = (AppListAdapter) getListView().getAdapter();
        adapter.setNotifyOnChange(false);
        for (App app : appsToAdd) {
            ListItem listItem = getListItem(app);
            listItems.put(app.getPackageName(), listItem);
            adapter.add(listItem);
        }
        if (update) {
            adapter.notifyDataSetChanged();
        }
    }

    public void removeApp(String packageName) {
        ((AppListAdapter) getListView().getAdapter()).remove(listItems.get(packageName));
        listItems.remove(packageName);
    }

    public Set<String> getListedPackageNames() {
        return listItems.keySet();
    }

    public void clearApps() {
        listItems.clear();
        ((AppListAdapter) getListView().getAdapter()).clear();
    }

    public ListView getListView() {
        return listView;
    }
}
