package com.github.yeriomin.yalpstore;

import android.os.Build;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;

class OnAppClickListener implements AdapterView.OnItemClickListener {

    private AppListActivity activity;

    public OnAppClickListener(AppListActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        DetailsActivity.app = activity.getAppByListPosition(position);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ImageView iconView = view.findViewById(R.id.icon);
            String transitionName = activity.getString(R.string.details_transition_view_name);
            iconView.setTransitionName(transitionName);
            activity.startActivity(
                DetailsActivity.getDetailsIntent(activity, DetailsActivity.app.getPackageName()),
                ActivityOptionsCompat.makeSceneTransitionAnimation(activity, iconView, transitionName).toBundle()
            );
        } else {
            activity.startActivity(DetailsActivity.getDetailsIntent(activity, DetailsActivity.app.getPackageName()));
        }
    }
}
