package com.github.yeriomin.yalpstore;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.ImageView;

public class ImageDownloadTask extends AsyncTask<String, Void, Void> {

    protected ImageView view;
    private boolean fullSize;
    private Context context;
    private Drawable drawable;

    public void setView(ImageView view) {
        this.view = view;
    }

    public void setFullSize(boolean fullSize) {
        this.fullSize = fullSize;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.context = this.view.getContext();
        this.view.setImageDrawable(this.context.getResources().getDrawable(R.drawable.ic_placeholder));
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (null != this.drawable) {
            this.view.setImageDrawable(this.drawable);
        }
    }

    @Override
    protected Void doInBackground(String[] params) {
        BitmapManager manager = new BitmapManager(this.context);
        Bitmap bitmap = manager.getBitmap(params[0], fullSize);
        if (null != bitmap) {
            this.drawable = new BitmapDrawable(bitmap);
        }
        return null;
    }

}
