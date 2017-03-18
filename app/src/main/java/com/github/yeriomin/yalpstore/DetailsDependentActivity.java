package com.github.yeriomin.yalpstore;

import android.util.Log;

import com.github.yeriomin.yalpstore.model.App;

import java.util.Map;

abstract public class DetailsDependentActivity extends AppListActivity {

    static public App app;

    @Override
    protected void onResume() {
        super.onResume();

        this.data.clear();
        if (null == app) {
            Log.w(getClass().getName(), "No app stored");
            finish();
            return;
        }
        loadApps();
    }

    @Override
    protected Map<String, Object> formatApp(App app) {
        Map<String, Object> map = super.formatApp(app);
        map.put(LINE2, getString(R.string.list_line_2_search, app.getInstalls(), app.getRating().getAverage(), app.getUpdated()));
        String ads = getString(app.containsAds() ? R.string.list_app_has_ads : R.string.list_app_no_ads);
        String gsf = getString(app.getDependencies().isEmpty() ? R.string.list_app_independent_from_gsf : R.string.list_app_depends_on_gsf);
        map.put(LINE3, getString(R.string.list_line_3_search, app.getPrice(), ads, gsf));
        map.put(ICON, app.getIconUrl());
        return map;
    }
}
