package com.github.yeriomin.yalpstore;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

import java.util.List;

class ImageAdapter extends BaseAdapter {

    protected Context context;
    protected List<String> screenshotUrls;
    protected int screenWidth;

    ImageAdapter(Context context, List<String> screenshotUrls, int screenWidth) {
        this.context = context;
        this.screenshotUrls = screenshotUrls;
        this.screenWidth = screenWidth;
    }

    @Override
    public int getCount() {
        return screenshotUrls.size();
    }

    @Override
    public Object getItem(int position) {
        return screenshotUrls.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageDownloadTask task = getTask();
        ImageView imageView = new ImageView(context);
        task.setView(imageView);
        task.execute((String) getItem(position));
        return imageView;
    }

    protected ImageDownloadTask getTask() {
        ImageDownloadTask task = new ImageDownloadTask() {

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Bitmap bitmap = ((BitmapDrawable) this.view.getDrawable()).getBitmap();
                int w = screenWidth;
                int h = screenWidth;
                if (null != bitmap) {
                    w = Math.min(w, bitmap.getWidth());
                    h = Math.min(h, bitmap.getHeight());
                }
                this.view.setLayoutParams(new Gallery.LayoutParams(w, h));
                this.view.setScaleType(ImageView.ScaleType.FIT_CENTER);
                if (this.view.getParent() instanceof Gallery) {
                    Gallery gallery = (Gallery) this.view.getParent();
                    gallery.setMinimumHeight(Math.max(gallery.getMeasuredHeight(), h));
                }
            }
        };
        task.setFullSize(true);
        return task;
    }
}
