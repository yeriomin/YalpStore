package in.dragons.galaxy.task.playstore;

import android.util.Log;

import com.github.yeriomin.playstoreapi.GooglePlayAPI;

import java.io.IOException;

import in.dragons.galaxy.DetailsActivity;
import in.dragons.galaxy.fragment.details.Review;

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
            Log.e(DetailsActivity.class.getSimpleName(), "Error deleting the review: " + getException().getMessage());
        }
    }
}
