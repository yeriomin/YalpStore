package com.github.yeriomin.yalpstore.task.playstore;

import android.util.Log;

import com.github.yeriomin.playstoreapi.GooglePlayAPI;
import com.github.yeriomin.yalpstore.DetailsActivity;
import com.github.yeriomin.yalpstore.fragment.details.Review;

import java.io.IOException;

public class ReviewDeleteTask extends PlayStorePayloadTask<Void> {

    private Review fragment;

    public void setFragment(Review fragment) {
        this.fragment = fragment;
    }

    @Override
    protected Void getResult(GooglePlayAPI api, String... packageNames) throws IOException {
        api.deleteReview(packageNames[0]);
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        if (success()) {
            fragment.clearUserReview();
        } else {
            Log.e(DetailsActivity.class.getName(), "Error deleting the review: " + getException().getMessage());
        }
    }
}
