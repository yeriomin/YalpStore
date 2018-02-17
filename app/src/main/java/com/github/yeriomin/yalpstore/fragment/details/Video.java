package com.github.yeriomin.yalpstore.fragment.details;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.view.View;

import com.github.yeriomin.yalpstore.ContextUtil;
import com.github.yeriomin.yalpstore.DetailsActivity;
import com.github.yeriomin.yalpstore.R;
import com.github.yeriomin.yalpstore.model.App;
import com.github.yeriomin.yalpstore.view.UriOnClickListener;

public class Video extends Abstract {

    public Video(DetailsActivity activity, App app) {
        super(activity, app);
    }

    @Override
    public void draw() {
        if (TextUtils.isEmpty(app.getVideoUrl())) {
            return;
        }
        prepareLink(activity.findViewById(R.id.video));
    }

    private void prepareLink(View linkView) {
        linkView.setVisibility(View.VISIBLE);
        linkView.setOnClickListener(new UriOnClickListener(activity, app.getVideoUrl()) {
            @Override
            protected void onActivityNotFound(ActivityNotFoundException e) {
                super.onActivityNotFound(e);
                ((ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE)).setText(app.getVideoUrl());
                ContextUtil.toast(context.getApplicationContext(), R.string.about_copied_to_clipboard);
            }
        });
    }
}
