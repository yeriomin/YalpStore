package com.github.yeriomin.yalpstore;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.yeriomin.yalpstore.model.App;

import java.util.Map;

public class SearchResultActivity extends AppListActivity {

    private String query;
    private String categoryId = CategoryManager.TOP;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        String newQuery = getQuery(intent);
        if (null != newQuery && !newQuery.equals(this.query)) {
            this.categoryId = CategoryManager.TOP;
            this.query = newQuery;
            this.data.clear();
            setTitle(getString(R.string.activity_title_search, this.query));
            loadApps();
            ((SimpleAdapter) getListAdapter()).notifyDataSetChanged();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.query = getQuery(getIntent());
        setTitle(getString(R.string.activity_title_search, this.query));

        super.onCreate(savedInstanceState);
        new CategoryManager(this).fill((Spinner) findViewById(R.id.filter));
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
                if (totalItemCount > 0 && loadMore && lastLastitem != lastItem) {
                    lastLastitem = lastItem;
                    loadApps();
                }
            }
        });
    }

    @Override
    protected Map<String, Object> formatApp(App app) {
        Map<String, Object> map = super.formatApp(app);
        String updated = app.getUpdated().isEmpty() ? getString(R.string.list_incompatible) : app.getUpdated();
        map.put(LINE2, getString(R.string.list_line_2_search, app.getInstalls(), app.getRating().getAverage(), updated));
        map.put(ICON, app.getIconUrl());
        return map;
    }

    public void setCategoryId(String categoryId) {
        if (!categoryId.equals(this.categoryId)) {
            this.categoryId = categoryId;
            data.clear();
            loadApps();
        }
    }

    public void loadApps() {
        SearchTask task = new SearchTask() {

            @Override
            protected void onPostExecute(Throwable e) {
                super.onPostExecute(e);
                addApps(apps);
            }
        };
        task.setContext(this);
        task.setErrorView((TextView) getListView().getEmptyView());
        task.prepareDialog(R.string.dialog_message_loading_app_list_search, R.string.dialog_title_loading_app_list_search);
        task.setCategoryManager(new CategoryManager(this));
        task.execute(query, categoryId);
    }

    private String getQuery(Intent intent) {
        if (intent.getScheme() != null
            && (intent.getScheme().equals("market")
            || intent.getScheme().equals("http")
            || intent.getScheme().equals("https"))
        ) {
            return intent.getData().getQueryParameter("q");
        }
        if (intent.getAction().equals(Intent.ACTION_SEARCH)) {
            return intent.getStringExtra(SearchManager.QUERY);
        } else if (intent.getAction().equals(Intent.ACTION_VIEW)) {
            return intent.getDataString();
        }
        return null;
    }
}
