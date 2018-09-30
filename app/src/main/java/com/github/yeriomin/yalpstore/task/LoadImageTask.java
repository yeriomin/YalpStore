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

package com.github.yeriomin.yalpstore.task;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.github.yeriomin.yalpstore.BitmapManager;
import com.github.yeriomin.yalpstore.NetworkUtil;
import com.github.yeriomin.yalpstore.PreferenceUtil;
import com.github.yeriomin.yalpstore.R;
import com.github.yeriomin.yalpstore.model.ImageSource;

public class LoadImageTask extends LowCpuIntensityTask<Void, Void, Void> {

    protected ImageView imageView;
    private ImageSource imageSource;
    private Drawable drawable;
    private String tag;
    private boolean placeholder = true;
    private boolean forceLoadImage = false;
    private int fadeInMillis = 0;

    public LoadImageTask() {

    }

    public LoadImageTask(ImageView imageView) {
        setImageView(imageView);
    }

    public LoadImageTask setImageView(ImageView imageView) {
        this.imageView = imageView;
        tag = (String) imageView.getTag();
        return this;
    }

    public LoadImageTask setImageSource(ImageSource imageSource) {
        this.imageSource = imageSource;
        return this;
    }

    public LoadImageTask setPlaceholder(boolean placeholder) {
        this.placeholder = placeholder;
        return this;
    }

    public void setForceLoadImage(boolean forceLoadImage) {
        this.forceLoadImage = forceLoadImage;
    }

    public LoadImageTask setFadeInMillis(int fadeInMillis) {
        this.fadeInMillis = fadeInMillis;
        return this;
    }

    @Override
    protected void onPreExecute() {
        if (placeholder) {
            imageView.setImageDrawable(imageView.getContext().getResources().getDrawable(R.drawable.ic_placeholder));
        }
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (null != imageView.getTag() && !imageView.getTag().equals(tag)) {
            return;
        }
        if (null != drawable) {
            if (sameAsLoaded()) {
                return;
            }
            if (fadeInMillis > 0) {
                fadeOut();
            }
            imageView.setImageDrawable(drawable);
            imageView.setClickable(false);
            if (fadeInMillis > 0) {
                fadeIn();
            }
        } else if (noImages()) {
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LoadImageTask task = new LoadImageTask();
                    task.setImageView(imageView);
                    task.setFadeInMillis(fadeInMillis);
                    task.setPlaceholder(placeholder);
                    task.setForceLoadImage(true);
                    task.setImageSource(imageSource);
                    task.executeOnExecutorIfPossible();
                }
            });
        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        if (null != imageSource.getApplicationInfo()) {
            drawable = imageView.getContext().getPackageManager().getApplicationIcon(imageSource.getApplicationInfo());
        } else if (!TextUtils.isEmpty(imageSource.getUrl())) {
            BitmapManager bitmapManager = new BitmapManager(imageView.getContext());
            bitmapManager.setNoImages(noImages());
            Bitmap bitmap = bitmapManager.getBitmap(imageSource.getUrl(), imageSource.isFullSize());
            if (null != bitmap || !noImages()) {
                drawable = getDrawable(bitmap);
            }
        }
        return null;
    }

    protected Drawable getDrawable(Bitmap bitmap) {
        return new BitmapDrawable(bitmap);
    }

    private void fadeIn() {
        imageView.setVisibility(View.VISIBLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            imageView.setAlpha(0.0f);
            imageView.animate().setDuration(fadeInMillis).withLayer().alpha(1.0f);
        } else {
            Animation a = new AlphaAnimation(0.0f, 1.0f);
            a.setDuration(fadeInMillis);
            imageView.startAnimation(a);
        }
    }

    private void fadeOut() {
        if (!placeholder) {
            imageView.setVisibility(View.INVISIBLE);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            imageView.animate().alpha(0.0f).setDuration(fadeInMillis).withLayer();
        } else {
            Animation a = new AlphaAnimation(1.0f, 0.0f);
            a.setDuration(fadeInMillis);
            imageView.startAnimation(a);
        }
    }

    private boolean noImages() {
        return !forceLoadImage
            && NetworkUtil.isMetered(imageView.getContext())
            && PreferenceUtil.getBoolean(imageView.getContext(), PreferenceUtil.PREFERENCE_NO_IMAGES)
        ;
    }

    private boolean sameAsLoaded() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1
            && drawable instanceof BitmapDrawable
            && imageView.getDrawable() instanceof BitmapDrawable
            && ((BitmapDrawable) imageView.getDrawable()).getBitmap() != null
            && ((BitmapDrawable) imageView.getDrawable()).getBitmap().sameAs(((BitmapDrawable) drawable).getBitmap())
        ;
    }
}
