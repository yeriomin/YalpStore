package com.github.yeriomin.yalpstore;

import android.content.Context;
import android.widget.Gallery;
import android.widget.ImageView;

import java.util.List;

class FullscreenImageAdapter extends ImageAdapter {

    private int screenHeight;

    FullscreenImageAdapter(Context context, List<String> screenshotUrls, int screenWidth, int screenHeight) {
        super(context, screenshotUrls, screenWidth);
        this.screenHeight = screenHeight;
    }

    @Override
    protected ImageDownloadTask getTask() {
        ImageDownloadTask task = new ImageDownloadTask() {

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                this.view.setLayoutParams(new Gallery.LayoutParams(screenWidth, screenHeight));
                this.view.setScaleType(ImageView.ScaleType.FIT_CENTER);
            }
        };
        task.setFullSize(true);
        return task;
    }
}
