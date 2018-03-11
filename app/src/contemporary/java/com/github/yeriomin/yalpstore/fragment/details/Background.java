package com.github.yeriomin.yalpstore.fragment.details;

import android.support.design.widget.CollapsingToolbarLayout;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.yeriomin.yalpstore.DetailsActivity;
import com.github.yeriomin.yalpstore.NetworkState;
import com.github.yeriomin.yalpstore.R;
import com.github.yeriomin.yalpstore.model.App;
import com.github.yeriomin.yalpstore.task.LoadImageTask;

public class Background extends Abstract {

    private static final int BACKGROUND_IMAGE_HEIGHT = 512;

    public Background(DetailsActivity activity, App app) {
        super(activity, app);
    }

    @Override
    public void draw() {
        View background = activity.findViewById(R.id.background);
        CollapsingToolbarLayout collapsingToolbarLayout = activity.findViewById(R.id.collapsing_toolbar_layout);
        collapsingToolbarLayout.setExpandedTitleColor(activity.getResources().getColor(android.R.color.transparent));
        if (!NetworkState.isNetworkAvailable(activity) || (!app.isInPlayStore() && !TextUtils.isEmpty(app.getDeveloperName()) && null == app.getPageBackgroundImage())) {
            collapsingToolbarLayout.setTitleEnabled(false);
            collapsingToolbarLayout.getLayoutParams().height = CollapsingToolbarLayout.LayoutParams.MATCH_PARENT;
            background.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
            background.setVisibility(View.GONE);
        } else {
            collapsingToolbarLayout.setTitleEnabled(true);
            collapsingToolbarLayout.getLayoutParams().height = BACKGROUND_IMAGE_HEIGHT;
            background.getLayoutParams().height = BACKGROUND_IMAGE_HEIGHT;
            background.setVisibility(View.VISIBLE);
            if (null == ((ImageView) background).getDrawable() && null != app.getPageBackgroundImage()) {
                new LoadImageTask((ImageView) background).setPlaceholder(false).setFadeInMillis(500).execute(app.getPageBackgroundImage());
            }
        }
    }
}
