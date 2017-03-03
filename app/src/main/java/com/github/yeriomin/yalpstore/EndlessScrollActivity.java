package com.github.yeriomin.yalpstore;

import android.os.Bundle;
import android.widget.TextView;

import com.github.yeriomin.yalpstore.model.App;

import java.util.Map;

abstract public class EndlessScrollActivity extends AppListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((TextView) getListView().getEmptyView()).setText(getString(R.string.list_empty_search));
        onNewIntent(getIntent());
        getListView().setOnScrollListener(new ScrollEdgeListener() {
            protected void loadMore() {
                loadApps();
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

    protected GoogleApiAsyncTask prepareTask(GoogleApiAsyncTask task) {
        task.setContext(this);
        task.setErrorView((TextView) getListView().getEmptyView());
        if (data.isEmpty()) {
            task.prepareDialog(R.string.dialog_message_loading_app_list_search, R.string.dialog_title_loading_app_list_search);
        } else {
            task.setProgressIndicator(findViewById(R.id.progress));
        }
        return task;
    }
}
