package com.dragons.aurora.task.playstore;

import android.util.Log;

import com.dragons.aurora.activities.DetailsActivity;
import com.dragons.aurora.model.Review;
import com.dragons.aurora.model.ReviewBuilder;
import com.dragons.aurora.playstoreapiv2.GooglePlayAPI;
import com.dragons.aurora.playstoreapiv2.ReviewResponse;

import java.io.IOException;

public class ReviewAddTask extends PlayStorePayloadTask<Review> {

    private com.dragons.aurora.fragment.details.Review fragment;
    private String packageName;
    private Review review;

    public void setFragment(com.dragons.aurora.fragment.details.Review fragment) {
        this.fragment = fragment;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public void setReview(com.dragons.aurora.model.Review review) {
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
