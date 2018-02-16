package in.dragons.galaxy;

import android.util.Log;

import com.github.yeriomin.playstoreapi.GooglePlayAPI;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import in.dragons.galaxy.model.Review;
import in.dragons.galaxy.model.ReviewBuilder;

public class ReviewRetrieverIterator extends ReviewIterator {

    static private final int PAGE_SIZE = 15;
    protected boolean hasNext = true;

    @Override
    public boolean hasNext() {
        return hasNext;
    }

    @Override
    public List<Review> next() {
        page++;
        List<Review> list = new ArrayList<>();
        try {
            list.addAll(getReviews(packageName, PAGE_SIZE * page, PAGE_SIZE));
            if (list.size() < PAGE_SIZE) {
                hasNext = false;
            }
        } catch (IOException e) {
            // Review list does not seem important enough to let tha app crash if something happens here
            // TODO: It is unclear if this error even should be shown in the UI
            Log.i(getClass().getSimpleName(), e.getClass().getName() + ": " + e.getMessage());
        }
        return list;
    }

    private List<Review> getReviews(String packageId, int offset, int numberOfResults) throws IOException {
        List<Review> reviews = new ArrayList<>();
        for (com.github.yeriomin.playstoreapi.Review review : new PlayStoreApiAuthenticator(context).getApi().reviews(
                packageId,
                GooglePlayAPI.REVIEW_SORT.HELPFUL,
                offset,
                numberOfResults
        ).getGetResponse().getReviewList()) {
            reviews.add(ReviewBuilder.build(review));
        }
        return reviews;
    }
}
