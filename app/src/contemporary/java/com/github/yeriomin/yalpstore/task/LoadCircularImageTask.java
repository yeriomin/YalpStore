package com.github.yeriomin.yalpstore.task;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.widget.ImageView;

public class LoadCircularImageTask extends LoadImageTask {

    private boolean cropCircle = false;

    public LoadImageTask setCropCircle(boolean cropCircle) {
        this.cropCircle = cropCircle;
        return this;
    }

    public LoadCircularImageTask(ImageView imageView) {
        super(imageView);
    }

    @Override
    protected Drawable getDrawable(Bitmap bitmap) {
        if (cropCircle) {
            RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(imageView.getResources(), bitmap);
            roundedBitmapDrawable.setCircular(true);
            roundedBitmapDrawable.setAntiAlias(true);
            return roundedBitmapDrawable;
        } else {
            return super.getDrawable(bitmap);
        }
    }
}
