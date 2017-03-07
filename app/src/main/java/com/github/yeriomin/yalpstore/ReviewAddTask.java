package com.github.yeriomin.yalpstore;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.github.yeriomin.yalpstore.model.Review;

import java.io.IOException;

class ReviewAddTask extends AsyncTask<Review, Void, Throwable> {

    private Context context;
    private ReviewManager manager;
    private Review review;
    private String packageName;

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    ReviewAddTask(Context context, ReviewManager manager) {
        this.context = context;
        this.manager = manager;
    }

    @Override
    protected Throwable doInBackground(Review... params) {
        PlayStoreApiWrapper wrapper = new PlayStoreApiWrapper(context);
        try {
            review = wrapper.addOrEditReview(packageName, params[0]);
        } catch (IOException e) {
            return e;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Throwable e) {
        if (null == e) {
            manager.fillUserReview(review);
        } else {
            Log.e(DetailsActivity.class.getName(), "Error adding the review: " + e.getMessage());
        }
    }
}
