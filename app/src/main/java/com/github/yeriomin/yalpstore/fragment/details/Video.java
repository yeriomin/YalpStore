package com.github.yeriomin.yalpstore.fragment.details;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.github.yeriomin.yalpstore.DetailsActivity;
import com.github.yeriomin.yalpstore.R;
import com.github.yeriomin.yalpstore.model.App;

public class Video extends Abstract {

    public Video(DetailsActivity activity, App app) {
        super(activity, app);
    }

    @Override
    public void draw() {
        if (TextUtils.isEmpty(app.getVideoUrl())) {
            return;
        }
        TextView systemAppInfo = activity.findViewById(R.id.video);
        systemAppInfo.setVisibility(View.VISIBLE);
        systemAppInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(app.getVideoUrl())));
            }
        });
    }
}
