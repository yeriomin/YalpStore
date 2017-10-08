package com.github.yeriomin.yalpstore.fragment.details;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.github.yeriomin.yalpstore.DetailsActivity;
import com.github.yeriomin.yalpstore.LogHelper;
import com.github.yeriomin.yalpstore.R;
import com.github.yeriomin.yalpstore.model.App;

public class Share extends Abstract {

    private static final String TAG = Share.class.getSimpleName();
    static private String PLAYSTORE_LINK_PREFIX = "https://play.google.com/store/apps/details?id=";

    public Share(DetailsActivity activity, App app) {
        super(activity, app);
    }

    @Override
    public void draw() {
        TextView share = (TextView) activity.findViewById(R.id.share);
        share.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, app.getDisplayName());
                String pkgUrl = PLAYSTORE_LINK_PREFIX + app.getPackageName();
                i.putExtra(Intent.EXTRA_TEXT, pkgUrl);
                activity.startActivity(Intent.createChooser(i, activity.getString(R.string.details_share)));
                LogHelper.i(TAG, "实际开启DirectDownloadActivity，pkgUrl->" + pkgUrl);
            }
        });
    }
}
