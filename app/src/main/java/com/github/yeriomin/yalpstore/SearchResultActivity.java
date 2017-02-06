package com.github.yeriomin.yalpstore;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.github.yeriomin.yalpstore.model.App;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SearchResultActivity extends AppListActivity {

    private String query;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        String newQuery = getQuery(intent);
        if (null != newQuery && !newQuery.equals(this.query)) {
            this.data.clear();
            this.query = newQuery;
            setTitle(getString(R.string.activity_title_search, this.query));
            loadApps();
            ((SimpleAdapter) getListAdapter()).notifyDataSetChanged();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.query = getQuery(getIntent());

        super.onCreate(savedInstanceState);
        loadApps();

        ((TextView) getListView().getEmptyView()).setText(getString(R.string.list_empty_search));
        getListView().setOnScrollListener(new ListView.OnScrollListener() {

            private int lastLastitem;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {}

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int lastItem = firstVisibleItem + visibleItemCount;
                boolean loadMore = lastItem >= totalItemCount;
                if (totalItemCount > 0 && loadMore) {
                    if (lastLastitem != lastItem) {
                        lastLastitem = lastItem;
                        loadApps();
                    }
                }
            }
        });
    }

    @Override
    protected Map<String, Object> formatApp(App app) {
        Map<String, Object> map = super.formatApp(app);
        map.put(LINE2, getString(R.string.list_line_2_search, app.getInstalls(), app.getRating().getAverage(), app.getUpdated()));
        map.put(ICON, app.getIconUrl());
        return map;
    }

    protected void loadApps() {
        GoogleApiAsyncTask task = new GoogleApiAsyncTask() {

            private List<App> apps = new ArrayList<>();
            private Set<String> installedPackageNames = new HashSet<>();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                List<App> installed = UpdatableAppsTask.getInstalledApps(context);
                for (App installedApp: installed) {
                    installedPackageNames.add(installedApp.getPackageName());
                }
            }

            @Override
            protected Throwable doInBackground(String... params) {
                PlayStoreApiWrapper wrapper = new PlayStoreApiWrapper(getApplicationContext());
                try {
                    PlayStoreApiWrapper.AppSearchResultIterator iterator = wrapper.getSearchIterator(query);
                    if (iterator.hasNext()) {
                        apps.addAll(iterator.next());
                    }
                    for (App app: apps) {
                        if (installedPackageNames.contains(app.getPackageName())) {
                            app.setInstalled(true);
                        }
                    }
                } catch (Throwable e) {
                    return e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Throwable e) {
                super.onPostExecute(e);
                addApps(apps);
            }
        };
        task.setContext(this);
        task.setErrorView((TextView) getListView().getEmptyView());
        task.prepareDialog(R.string.dialog_message_loading_app_list_search, R.string.dialog_title_loading_app_list_search);
        task.execute();
    }

    private String getQuery(Intent intent) {
        if (intent.getScheme() != null
            && (intent.getScheme().equals("market")
            || intent.getScheme().equals("http")
            || intent.getScheme().equals("https"))
        ) {
            return intent.getData().getQueryParameter("q");
        }
        switch (intent.getAction()) {
            case Intent.ACTION_SEARCH:
                return intent.getStringExtra(SearchManager.QUERY);
            case Intent.ACTION_VIEW:
                return intent.getDataString();
        }
        return null;
    }

}
