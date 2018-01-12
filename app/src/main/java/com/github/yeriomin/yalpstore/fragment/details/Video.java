package com.github.yeriomin.yalpstore.fragment.details;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.github.yeriomin.yalpstore.ContextUtil;
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
        TextView videoLink = activity.findViewById(R.id.video);
        videoLink.setVisibility(View.VISIBLE);
        videoLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(app.getVideoUrl())));
                } catch (ActivityNotFoundException e) {
                    ((ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE)).setText(app.getVideoUrl());
                    ContextUtil.toast(v.getContext().getApplicationContext(), R.string.about_copied_to_clipboard);
                }
            }
        });
    }
}
