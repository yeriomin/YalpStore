package com.github.yeriomin.yalpstore.fragment.details;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.github.yeriomin.yalpstore.ContextUtil;
import com.github.yeriomin.yalpstore.DetailsActivity;
import com.github.yeriomin.yalpstore.R;
import com.github.yeriomin.yalpstore.model.App;
import com.squareup.picasso.Picasso;

public class Video extends Abstract {

    public Video(DetailsActivity activity, App app) {
        super(activity, app);
    }

    private String getID(String URL)
    {
        if(URL.contains("/youtu.be/"))
            URL=URL.substring(URL.lastIndexOf('/')+1,URL.length());
        else if(URL.contains("feature"))
            URL=URL.substring(URL.indexOf('=')+1,URL.lastIndexOf('&'));
        else
            URL=URL.substring(URL.indexOf('=')+1,URL.length());
        return URL;
    }

    @Override
    public void draw() {
        if (TextUtils.isEmpty(app.getVideoUrl())) {
            return;
        }

        String vID =getID(app.getVideoUrl());
        String URL="https://img.youtube.com/vi/"+vID+"/maxresdefault.jpg";

        ImageView imageView = activity.findViewById(R.id.thumbnail);
        Picasso.with(activity).load(URL).into(imageView);

        activity.findViewById(R.id.app_video).setVisibility(View.VISIBLE);

        ImageView play = activity.findViewById(R.id.thumbnail);
        play.setOnClickListener(new View.OnClickListener() {
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
