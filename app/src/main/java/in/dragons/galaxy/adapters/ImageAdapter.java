package in.dragons.galaxy.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

import java.util.List;

import in.dragons.galaxy.model.ImageSource;
import in.dragons.galaxy.task.LoadImageTask;

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

        int screenWidth;

        AdapterLoadImageTask(int screenWidth) {
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
