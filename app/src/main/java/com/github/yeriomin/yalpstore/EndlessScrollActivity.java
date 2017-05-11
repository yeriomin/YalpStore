package com.github.yeriomin.yalpstore;

import android.os.Bundle;
import android.widget.TextView;

import com.github.yeriomin.yalpstore.model.App;
import com.github.yeriomin.yalpstore.view.ListItem;
import com.github.yeriomin.yalpstore.view.SearchResultAppBadge;

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

    protected GoogleApiAsyncTask prepareTask(GoogleApiAsyncTask task) {
        task.setContext(this);
        task.setErrorView((TextView) getListView().getEmptyView());
        if (getListView().getAdapter().isEmpty()) {
            task.prepareDialog(R.string.dialog_message_loading_app_list_search, R.string.dialog_title_loading_app_list_search);
        } else {
            task.setProgressIndicator(findViewById(R.id.progress));
        }
        return task;
    }
}
