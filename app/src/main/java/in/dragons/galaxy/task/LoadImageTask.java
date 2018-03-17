package in.dragons.galaxy.task;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.widget.ImageView;

import in.dragons.galaxy.BitmapManager;
import in.dragons.galaxy.NetworkState;
import in.dragons.galaxy.fragment.PreferenceFragment;
import in.dragons.galaxy.R;
import in.dragons.galaxy.model.ImageSource;

public class LoadImageTask extends AsyncTask<ImageSource, Void, Void> {

    protected ImageView imageView;
    private Drawable drawable;
    private String tag;

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

    @Override
    protected void onPreExecute() {
        imageView.setImageDrawable(imageView.getContext().getResources().getDrawable(R.drawable.ic_placeholder));
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
    protected Void doInBackground(ImageSource... params) {
        ImageSource imageSource = params[0];
        if (null != imageSource.getApplicationInfo()) {
            drawable = imageView.getContext().getPackageManager().getApplicationIcon(imageSource.getApplicationInfo());
        } else if (!TextUtils.isEmpty(imageSource.getUrl())) {
            Bitmap bitmap = new BitmapManager(imageView.getContext()).getBitmap(imageSource.getUrl(), imageSource.isFullSize());
            if (null != bitmap || !noImages()) {
                drawable = new BitmapDrawable(bitmap);
            }
        }
        return null;
    }

    private boolean noImages() {
        return NetworkState.isMetered(imageView.getContext()) && PreferenceFragment.getBoolean(imageView.getContext(), PreferenceFragment.PREFERENCE_NO_IMAGES);
    }
}
