package com.github.yeriomin.yalpstore.task.playstore;

import android.util.Log;

import com.github.yeriomin.playstoreapi.GooglePlayAPI;
import com.github.yeriomin.playstoreapi.ReviewResponse;
import com.github.yeriomin.yalpstore.DetailsActivity;
import com.github.yeriomin.yalpstore.model.Review;
import com.github.yeriomin.yalpstore.model.ReviewBuilder;

import java.io.IOException;

public class ReviewAddTask extends PlayStorePayloadTask<Review> {

    private com.github.yeriomin.yalpstore.fragment.details.Review fragment;
    private String packageName;
    private Review review;

    public void setFragment(com.github.yeriomin.yalpstore.fragment.details.Review fragment) {
        this.fragment = fragment;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public void setReview(com.github.yeriomin.yalpstore.model.Review review) {
        this.review = review;
    }

    @Override
    protected Review getResult(GooglePlayAPI api, String... arguments) throws IOException {
        ReviewResponse response = api.addOrEditReview(
            packageName,
            review.getComment(),
            review.getTitle(),
            review.getRating()
        );
        return ReviewBuilder.build(response.getUserReview());
    }

    @Override
    protected void onPostExecute(Review review) {
        if (success()) {
            fragment.fillUserReview(review);
        } else {
            Log.e(DetailsActivity.class.getSimpleName(), "Error adding the review: " + getException().getMessage());
            getException().printStackTrace();
        }
    }
}
