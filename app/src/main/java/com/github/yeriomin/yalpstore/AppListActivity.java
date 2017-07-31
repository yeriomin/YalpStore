package com.github.yeriomin.yalpstore;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.github.yeriomin.yalpstore.model.App;
import com.github.yeriomin.yalpstore.view.AppBadge;
import com.github.yeriomin.yalpstore.view.ListItem;
import com.github.yeriomin.yalpstore.view.ListSeparator;

import java.util.List;

abstract public class AppListActivity extends YalpStoreActivity {

    protected ListView listView;

    abstract protected void loadApps();
    abstract protected ListItem getListItem(App app);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.applist_activity_layout);

        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListItem listItem = (ListItem) getListView().getItemAtPosition(position);
                if (null == listItem || !(listItem instanceof AppBadge)) {
                    return;
                }
                App app = ((AppBadge) listItem).getApp();
                DetailsActivity.app = app;
                startActivity(DetailsActivity.getDetailsIntent(AppListActivity.this, app.getPackageName()));
            }
        });
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        View emptyView = findViewById(android.R.id.empty);
        listView = (ListView) findViewById(android.R.id.list);
        if (emptyView != null) {
            listView.setEmptyView(emptyView);
        }
        if (null == listView.getAdapter()) {
            listView.setAdapter(new AppListAdapter(this, R.layout.two_line_list_item_with_icon));
        }
    }

    protected void addSeparator(String label) {
        ListSeparator listSeparator = new ListSeparator();
        listSeparator.setLabel(label);
        ((AppListAdapter) getListView().getAdapter()).add(listSeparator);
    }

    protected void addApps(List<App> appsToAdd) {
        addApps(appsToAdd, true);
    }

    protected void addApps(List<App> appsToAdd, boolean update) {
        AppListAdapter adapter = (AppListAdapter) getListView().getAdapter();
        adapter.setNotifyOnChange(false);
        for (App app: appsToAdd) {
            adapter.add(getListItem(app));
        }
        if (update) {
            ((AppListAdapter) getListView().getAdapter()).notifyDataSetChanged();
        }
    }

    protected void clearApps() {
        ((AppListAdapter) getListView().getAdapter()).clear();
    }

    public ListView getListView() {
        return listView;
    }
}
