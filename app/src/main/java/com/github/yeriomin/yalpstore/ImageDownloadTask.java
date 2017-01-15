package com.github.yeriomin.yalpstore;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.ImageView;

class ImageDownloadTask extends AsyncTask<String, Void, Void> {

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
        this.view.setImageDrawable(this.context.getResources().getDrawable(android.R.drawable.sym_def_app_icon));
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        this.view.setImageDrawable(this.drawable);
    }

    @Override
    protected Void doInBackground(String[] params) {
        BitmapManager manager = new BitmapManager(this.context);
        this.drawable = new BitmapDrawable(manager.getBitmap(params[0], fullSize));
        return null;
    }

}
