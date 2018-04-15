package in.dragons.galaxy.fragment.details;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.v7.graphics.Palette;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import in.dragons.galaxy.ContextUtil;
import in.dragons.galaxy.activities.DetailsActivity;
import in.dragons.galaxy.R;
import in.dragons.galaxy.model.App;

public class Video extends Abstract {

    public Video(DetailsActivity activity, App app) {
        super(activity, app);
    }

    private String getID(String URL) {
        if (URL.contains("/youtu.be/"))
            URL = URL.substring(URL.lastIndexOf('/') + 1, URL.length());
        else if (URL.contains("feature"))
            URL = URL.substring(URL.indexOf('=') + 1, URL.lastIndexOf('&'));
        else
            URL = URL.substring(URL.indexOf('=') + 1, URL.length());
        return URL;
    }

    @Override
    public void draw() {
        if (TextUtils.isEmpty(app.getVideoUrl())) {
            return;
        }

        String vID = getID(app.getVideoUrl());
        String URL = "https://img.youtube.com/vi/" + vID + "/hqdefault.jpg";

        ImageView icon = activity.findViewById(R.id.icon);
        ImageView imageView = activity.findViewById(R.id.thumbnail);

        ImageView blank = new ImageView(activity);

        Picasso.with(activity).load("https://img.youtube.com/vi/afj914/hqdefault.jpg").into(blank);

        Picasso.with(activity)
                .load(URL)
                .fit()
                .placeholder(android.R.color.transparent)
                .centerCrop()
                .into(imageView);
        if (imageView.getDrawable() == blank.getDrawable()) {
            Bitmap bitmap = ((BitmapDrawable) icon.getDrawable()).getBitmap();
            if (bitmap != null)
                getPalette(bitmap, activity.findViewById(R.id.thumbnail));
        }
        activity.findViewById(R.id.app_video).setVisibility(View.VISIBLE);

        ImageView play = (ImageView) activity.findViewById(R.id.vid_play);
        play.setOnClickListener(v -> {
            try {
                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(app.getVideoUrl())));
            } catch (ActivityNotFoundException e) {
                ((ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE)).setText(app.getVideoUrl());
                ContextUtil.toast(v.getContext().getApplicationContext(), R.string.about_copied_to_clipboard);
            }
        });
    }

    private void getPalette(Bitmap bitmap, ImageView card) {
        Palette.from(bitmap)
                .generate(palette -> {
                    Palette.Swatch mySwatch = palette.getDarkVibrantSwatch();
                    if (mySwatch != null) {
                        card.setBackgroundColor(mySwatch.getRgb());
                    }
                });
    }

}