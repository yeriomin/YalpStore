package com.dragons.aurora.task.playstore;

import android.util.Log;

import com.dragons.aurora.ReviewStorageIterator;
import com.dragons.aurora.activities.DetailsActivity;
import com.dragons.aurora.model.Review;
import com.dragons.aurora.playstoreapiv2.GooglePlayAPI;

import java.io.IOException;
import java.util.List;

public class ReviewLoadTask extends PlayStorePayloadTask<List<Review>> {

    private ReviewStorageIterator iterator;
    private com.dragons.aurora.fragment.details.Review fragment;
    private boolean next;

    public void setIterator(ReviewStorageIterator iterator) {
        this.iterator = iterator;
    }

    public void setFragment(com.dragons.aurora.fragment.details.Review fragment) {
        this.fragment = fragment;
    }

    public void setNext(boolean next) {
        this.next = next;
    }

    @Override
    protected List<Review> getResult(GooglePlayAPI api, String... arguments) throws IOException {
        return next ? iterator.next() : iterator.previous();
    }

    @Override
    protected void onPostExecute(List<Review> reviews) {
        super.onPostExecute(reviews);
        if (success()) {
            fragment.showReviews(reviews);
        } else {
            Log.e(DetailsActivity.class.getSimpleName(), "Could not get reviews: " + getException().getMessage());
        }
    }
}