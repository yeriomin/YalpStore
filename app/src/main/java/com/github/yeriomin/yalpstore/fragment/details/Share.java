package com.github.yeriomin.yalpstore.fragment.details;

import android.content.Intent;

import com.github.yeriomin.yalpstore.DetailsActivity;
import com.github.yeriomin.yalpstore.R;
import com.github.yeriomin.yalpstore.model.App;
import com.github.yeriomin.yalpstore.view.IntentOnClickListener;

public class Share extends Abstract {

    static private String PLAYSTORE_LINK_PREFIX= "https://play.google.com/store/apps/details?id=";

    public Share(DetailsActivity activity, App app) {
        super(activity, app);
    }

    @Override
    public void draw() {
        activity.findViewById(R.id.share).setOnClickListener(new IntentOnClickListener(activity) {
            @Override
            protected Intent buildIntent() {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, app.getDisplayName());
                i.putExtra(Intent.EXTRA_TEXT, PLAYSTORE_LINK_PREFIX + app.getPackageName());
                return Intent.createChooser(i, activity.getString(R.string.details_share));
            }
        });
    }
}
