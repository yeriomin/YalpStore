package in.dragons.galaxy.task.playstore;

import android.util.Log;

import com.github.yeriomin.playstoreapi.GooglePlayAPI;

import java.io.IOException;
import java.util.List;

import in.dragons.galaxy.activities.DetailsActivity;
import in.dragons.galaxy.ReviewStorageIterator;
import in.dragons.galaxy.model.Review;

public class ReviewLoadTask extends PlayStorePayloadTask<List<Review>> {

    private ReviewStorageIterator iterator;
    private in.dragons.galaxy.fragment.details.Review fragment;
    private boolean next;

    public void setIterator(ReviewStorageIterator iterator) {
        this.iterator = iterator;
    }

    public void setFragment(in.dragons.galaxy.fragment.details.Review fragment) {
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