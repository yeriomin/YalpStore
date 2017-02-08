package com.github.yeriomin.yalpstore;

import android.app.SearchManager;
import android.content.Intent;
import android.view.View;

import com.github.yeriomin.yalpstore.model.App;


public class AppListsManager extends DetailsManager {

    public AppListsManager(DetailsActivity activity, App app) {
        super(activity, app);
    }

    @Override
    public void draw() {
        activity.findViewById(R.id.apps_by_this_dev).setOnClickListener(new AppListOnClickListener(SearchResultActivity.class) {
            @Override
            protected Intent getIntent() {
                Intent i = super.getIntent();
                i.setAction(Intent.ACTION_SEARCH);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.putExtra(SearchManager.QUERY, "pub:" + app.getDeveloper().getName());
                return i;
            }
        });
        activity.findViewById(R.id.similar_apps).setOnClickListener(new AppListOnClickListener(SimilarAppsActivity.class));
        activity.findViewById(R.id.users_also_installed).setOnClickListener(new AppListOnClickListener(UsersAlsoInstalledActivity.class));
    }

    private class AppListOnClickListener implements View.OnClickListener {

        Class activityClass;

        public AppListOnClickListener(Class activityClass) {
            this.activityClass = activityClass;
        }

        @Override
        public void onClick(View v) {
            activity.startActivity(getIntent());
        }

        protected Intent getIntent() {
            return new Intent(activity, activityClass);
        }
    }
}
