package com.github.yeriomin.yalpstore;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.github.yeriomin.yalpstore.fragment.details.Review;

import java.io.IOException;

class ReviewAddTask extends AsyncTask<com.github.yeriomin.yalpstore.model.Review, Void, Throwable> {

    private Context context;
    private Review manager;
    private com.github.yeriomin.yalpstore.model.Review review;
    private String packageName;

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    ReviewAddTask(Context context, Review manager) {
        this.context = context;
        this.manager = manager;
    }

    @Override
    protected Throwable doInBackground(com.github.yeriomin.yalpstore.model.Review... params) {
        PlayStoreApiWrapper wrapper = new PlayStoreApiWrapper(context);
        try {
            review = wrapper.addOrEditReview(packageName, params[0]);
        } catch (IOException e) {
            e.printStackTrace();
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
