package com.github.yeriomin.yalpstore;

import android.os.Bundle;
import android.text.TextUtils;
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
        String updated = TextUtils.isEmpty(app.getUpdated()) ? getString(R.string.list_incompatible) : app.getUpdated();
        map.put(LINE2, getString(R.string.list_line_2_search, app.getInstalls(), app.getRating().getAverage(), updated));
        String ads = getString(app.containsAds() ? R.string.list_app_has_ads : R.string.list_app_no_ads);
        String gsf = getString(app.getDependencies().isEmpty() ? R.string.list_app_independent_from_gsf : R.string.list_app_depends_on_gsf);
        map.put(LINE3, getString(R.string.list_line_3_search, app.getPrice(), ads, gsf));
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
