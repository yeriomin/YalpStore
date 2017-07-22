package com.github.yeriomin.yalpstore;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

import com.github.yeriomin.yalpstore.model.ImageSource;

import java.util.List;

public class ImageAdapter extends BaseAdapter {

    protected Context context;
    protected List<String> screenshotUrls;
    protected int screenWidth;

    public ImageAdapter(Context context, List<String> screenshotUrls, int screenWidth) {
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
        LoadImageTask task = getTask();
        ImageView imageView = new ImageView(context);
        task.setImageView(imageView);
        ImageSource source = new ImageSource((String) getItem(position));
        source.setFullSize(true);
        task.execute(source);
        return imageView;
    }

    protected LoadImageTask getTask() {
        return new AdapterLoadImageTask(screenWidth);
    }

    static class AdapterLoadImageTask extends LoadImageTask {

        protected int screenWidth;

        public AdapterLoadImageTask(int screenWidth) {
            this.screenWidth = screenWidth;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            int w = screenWidth;
            int h = screenWidth;
            if (null != bitmap) {
                w = Math.min(w, bitmap.getWidth());
                h = Math.min(h, bitmap.getHeight());
            }
            imageView.setLayoutParams(new Gallery.LayoutParams(w, h));
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            if (imageView.getParent() instanceof Gallery) {
                Gallery gallery = (Gallery) imageView.getParent();
                gallery.setMinimumHeight(Math.max(gallery.getMeasuredHeight(), h));
            }
        }
    }
}
