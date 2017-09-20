package com.github.yeriomin.yalpstore;

import com.github.yeriomin.playstoreapi.GooglePlayAPI;
import com.github.yeriomin.yalpstore.model.Review;
import com.github.yeriomin.yalpstore.model.ReviewBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
        try {
            List<Review> list = getReviews(packageName, PAGE_SIZE * page, PAGE_SIZE);
            if (list.size() < PAGE_SIZE) {
                hasNext = false;
            }
            return list;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Review> getReviews(String packageId, int offset, int numberOfResults) throws IOException {
        List<Review> reviews = new ArrayList<>();
        for (com.github.yeriomin.playstoreapi.Review review: new PlayStoreApiAuthenticator(context).getApi().reviews(
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
