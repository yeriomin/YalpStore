package com.github.yeriomin.yalpstore;

import android.app.SearchManager;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import com.github.yeriomin.yalpstore.model.App;


public class AppListsFragment extends DetailsFragment {

    public AppListsFragment(DetailsActivity activity, App app) {
        super(activity, app);
    }

    @Override
    public void draw() {
        drawAppsByThisDev();
        drawSimilarApps();
        drawUsersAlsoInstalled();
    }

    private void drawAppsByThisDev() {
        View appsByThisDev = activity.findViewById(R.id.apps_by_this_dev);
        if (TextUtils.isEmpty(app.getDeveloper().getName())) {
            appsByThisDev.setVisibility(View.GONE);
        } else {
            appsByThisDev.setVisibility(View.VISIBLE);
            appsByThisDev.setOnClickListener(new AppListOnClickListener(SearchResultActivity.class) {
                @Override
                protected Intent getIntent() {
                    Intent i = super.getIntent();
                    i.setAction(Intent.ACTION_SEARCH);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    i.putExtra(SearchManager.QUERY, "pub:" + app.getDeveloper().getName());
                    return i;
                }
            });
        }
    }

    private void drawSimilarApps() {
        View similarApps = activity.findViewById(R.id.similar_apps);
        if (DetailsDependentActivity.app.getSimilarApps().isEmpty()) {
            similarApps.setVisibility(View.GONE);
        } else {
            similarApps.setVisibility(View.VISIBLE);
            similarApps.setOnClickListener(new AppListOnClickListener(SimilarAppsActivity.class));
        }
    }

    private void drawUsersAlsoInstalled() {
        View alsoInstalled = activity.findViewById(R.id.users_also_installed);
        if (DetailsDependentActivity.app.getUsersAlsoInstalledApps().isEmpty()) {
            alsoInstalled.setVisibility(View.GONE);
        } else {
            alsoInstalled.setVisibility(View.VISIBLE);
            alsoInstalled.setOnClickListener(new AppListOnClickListener(UsersAlsoInstalledActivity.class));
        }
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
