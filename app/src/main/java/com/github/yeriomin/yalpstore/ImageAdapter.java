/*
 * Yalp Store
 * Copyright (C) 2018 Sergey Yeriomin <yeriomin@gmail.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.github.yeriomin.yalpstore;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

import com.github.yeriomin.yalpstore.model.ImageSource;
import com.github.yeriomin.yalpstore.task.LoadImageTask;

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
    public String getItem(int position) {
        return position >= 0 && position < screenshotUrls.size() ? screenshotUrls.get(position) : "";
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView = new ImageView(context);
        getTask()
            .setImageView(imageView)
            .setImageSource(new ImageSource(getItem(position)).setFullSize(true))
            .executeOnExecutorIfPossible()
        ;
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
            int w = screenWidth;
            int h = screenWidth;
            Drawable drawable = imageView.getDrawable();
            if (drawable instanceof BitmapDrawable) {
                Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                if (null != bitmap) {
                    w = Math.min(w, bitmap.getWidth());
                    h = Math.min(h, bitmap.getHeight());
                }
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
