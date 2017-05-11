package com.github.yeriomin.yalpstore;

import android.util.Log;

import com.github.yeriomin.yalpstore.model.App;
import com.github.yeriomin.yalpstore.view.ListItem;
import com.github.yeriomin.yalpstore.view.SearchResultAppBadge;

abstract public class DetailsDependentActivity extends AppListActivity {

    static public App app;

    @Override
    protected void onResume() {
        super.onResume();

        if (null == app) {
            Log.w(getClass().getName(), "No app stored");
            finish();
            return;
        }
        loadApps();
    }

    @Override
    protected ListItem getListItem(App app) {
        SearchResultAppBadge appBadge = new SearchResultAppBadge();
        appBadge.setApp(app);
        return appBadge;
    }
}
