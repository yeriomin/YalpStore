package in.dragons.galaxy.adapters;

import android.content.Context;
import android.widget.Gallery;
import android.widget.ImageView;

import java.util.List;

import in.dragons.galaxy.task.LoadImageTask;

public class FullscreenImageAdapter extends ImageAdapter {

    private int screenHeight;

    public FullscreenImageAdapter(Context context, List<String> screenshotUrls, int screenWidth, int screenHeight) {
        super(context, screenshotUrls, screenWidth);
        this.screenHeight = screenHeight;
    }

    @Override
    protected LoadImageTask getTask() {
        return new FullscreenLoadImageTask(screenWidth, screenHeight);
    }

    static class FullscreenLoadImageTask extends AdapterLoadImageTask {

        private int screenHeight;

        private FullscreenLoadImageTask(int screenWidth, int screenHeight) {
            super(screenWidth);
            this.screenHeight = screenHeight;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            imageView.setLayoutParams(new Gallery.LayoutParams(screenWidth, screenHeight));
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }
    }
}
