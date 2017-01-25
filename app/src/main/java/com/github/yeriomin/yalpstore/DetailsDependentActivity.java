package com.github.yeriomin.yalpstore;

import com.github.yeriomin.yalpstore.model.App;

import java.util.Map;

abstract public class DetailsDependentActivity extends AppListActivity {

    static protected App app;

    protected App getApp() {
        return DetailsDependentActivity.app;
    }

    @Override
    protected void onResume() {
        super.onResume();

        this.data.clear();
        loadApps();
    }

    @Override
    protected Map<String, Object> formatApp(App app) {
        Map<String, Object> map = super.formatApp(app);
        map.put(LINE2, getString(R.string.list_line_2_search, app.getInstalls(), app.getRating().getAverage(), app.getUpdated()));
        map.put(ICON, app.getIconUrl());
        return map;
    }
}
