package com.github.yeriomin.yalpstore;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.widget.ImageView;

import com.github.yeriomin.yalpstore.model.IconInfo;

public class LoadIconTask extends AsyncTask<IconInfo, Void, Void> {

    private ImageView imageView;
    private Context context;
    private Drawable drawable;
    private String tag;

    public LoadIconTask(ImageView imageView) {
        this.imageView = imageView;
        tag = (String) imageView.getTag();
        context = imageView.getContext();
    }

    @Override
    protected void onPreExecute() {
        imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_placeholder));
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (null != imageView.getTag() && !imageView.getTag().equals(tag)) {
            return;
        }
        if (null != drawable) {
            imageView.setImageDrawable(drawable);
        }
    }

    @Override
    protected Void doInBackground(IconInfo... params) {
        IconInfo iconInfo = params[0];
        if (null != iconInfo.getApplicationInfo()) {
            drawable = context.getPackageManager().getApplicationIcon(iconInfo.getApplicationInfo());
        } else if (!TextUtils.isEmpty(iconInfo.getUrl())) {
            drawable = new BitmapDrawable(new BitmapManager(context).getBitmap(iconInfo.getUrl()));
        }
        return null;
    }
}
