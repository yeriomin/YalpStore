package com.github.yeriomin.yalpstore.fragment.details;

import android.app.SearchManager;
import android.content.Intent;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.yeriomin.yalpstore.ClusterActivity;
import com.github.yeriomin.yalpstore.DetailsActivity;
import com.github.yeriomin.yalpstore.R;
import com.github.yeriomin.yalpstore.SearchActivity;
import com.github.yeriomin.yalpstore.model.App;

public class AppLists extends Abstract {

    public AppLists(DetailsActivity activity, App app) {
        super(activity, app);
    }

    @Override
    public void draw() {
        LinearLayout relatedLinksLayout = (LinearLayout) activity.findViewById(R.id.related_links);
        boolean developerLinkFound = false;
        for (final String label: app.getRelatedLinks().keySet()) {
            relatedLinksLayout.setVisibility(View.VISIBLE);
            relatedLinksLayout.addView(buildLinkView(label, app.getRelatedLinks().get(label)));
            if (label.contains(app.getDeveloperName())) {
                developerLinkFound = true;
            }
        }
        if (!developerLinkFound && !TextUtils.isEmpty(app.getDeveloperName())) {
            addAppsByThisDeveloper();
        }
    }

    private TextView buildLinkView(final String label, final String url) {
        TextView linkView = new TextView(activity);
        linkView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_chevron_right, 0, 0, 0);
        linkView.setText(label);
        linkView.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
        linkView.setPadding(0,6,0,0);
        linkView.setGravity(Gravity.CENTER_VERTICAL);
        linkView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClusterActivity.start(activity, url, label);
            }
        });
        return linkView;
    }

    private void addAppsByThisDeveloper() {
        ImageView imageView = activity.findViewById(R.id.apps_by_same_developer);
        //textView.setText(activity.getString(R.string.apps_by, app.getDeveloperName()));
        imageView.setVisibility(View.VISIBLE);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, SearchActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setAction(Intent.ACTION_SEARCH);
                intent.putExtra(SearchManager.QUERY, SearchActivity.PUB_PREFIX + app.getDeveloperName());
                activity.startActivity(intent);
            }
        });
    }
}
