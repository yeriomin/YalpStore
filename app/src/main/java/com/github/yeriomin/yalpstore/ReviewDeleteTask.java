package com.github.yeriomin.yalpstore;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;

class ReviewDeleteTask extends AsyncTask<String, Void, Throwable> {

    private Context context;
    private ReviewFragment manager;

    ReviewDeleteTask(Context context, ReviewFragment manager) {
        this.context = context;
        this.manager = manager;
    }

    @Override
    protected void onPostExecute(Throwable e) {
        if (null == e) {
            manager.clearUserReview();
        } else {
            Log.e(DetailsActivity.class.getName(), "Error deleting the review: " + e.getMessage());
        }
    }

    @Override
    protected Throwable doInBackground(String... params) {
        PlayStoreApiWrapper wrapper = new PlayStoreApiWrapper(context);
        try {
            wrapper.deleteReview(params[0]);
        } catch (IOException e) {
            return e;
        }
        return null;
    }
}
