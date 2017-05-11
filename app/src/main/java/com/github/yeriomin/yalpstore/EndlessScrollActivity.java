package com.github.yeriomin.yalpstore;

import android.os.Bundle;
import android.widget.TextView;

import com.github.yeriomin.yalpstore.model.App;
import com.github.yeriomin.yalpstore.view.ListItem;
import com.github.yeriomin.yalpstore.view.ProgressIndicator;
import com.github.yeriomin.yalpstore.view.SearchResultAppBadge;

import java.util.List;

abstract public class EndlessScrollActivity extends AppListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((TextView) getListView().getEmptyView()).setText(getString(R.string.list_empty_search));
        onNewIntent(getIntent());
        getListView().setOnScrollListener(new ScrollEdgeListener() {

            @Override
            protected void loadMore() {
                loadApps();
            }
        });
    }

    @Override
    protected ListItem getListItem(App app) {
        SearchResultAppBadge appBadge = new SearchResultAppBadge();
        appBadge.setApp(app);
        return appBadge;
    }

    @Override
    protected void addApps(List<App> appsToAdd) {
        AppListAdapter adapter = (AppListAdapter) getListView().getAdapter();
        if (!adapter.isEmpty()) {
            ListItem last = adapter.getItem(adapter.getCount() - 1);
            if (last instanceof ProgressIndicator) {
                adapter.remove(last);
            }
        }
        super.addApps(appsToAdd, false);
        if (!appsToAdd.isEmpty()) {
            adapter.add(new ProgressIndicator());
        }
        adapter.notifyDataSetChanged();
    }

    protected GoogleApiAsyncTask prepareTask(GoogleApiAsyncTask task) {
        task.setContext(this);
        task.setErrorView((TextView) getListView().getEmptyView());
        if (getListView().getAdapter().isEmpty()) {
            task.prepareDialog(R.string.dialog_message_loading_app_list_search, R.string.dialog_title_loading_app_list_search);
        }
        return task;
    }
}
