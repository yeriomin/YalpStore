package com.github.yeriomin.yalpstore;

import android.view.View;
import android.widget.AdapterView;

class OnAppClickListener implements AdapterView.OnItemClickListener {

    private AppListActivity activity;

    public OnAppClickListener(AppListActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        DetailsActivity.app = activity.getAppByListPosition(position);
        activity.startActivity(DetailsActivity.getDetailsIntent(activity, DetailsActivity.app.getPackageName()));
    }
}
