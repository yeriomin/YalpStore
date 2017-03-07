package com.github.yeriomin.yalpstore;

import com.github.yeriomin.yalpstore.model.Review;

import java.io.IOException;
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
        PlayStoreApiWrapper wrapper = new PlayStoreApiWrapper(context);
        try {
            List<Review> list = wrapper.getReviews(packageName, PAGE_SIZE * page, PAGE_SIZE);
            if (list.size() < PAGE_SIZE) {
                hasNext = false;
            }
            return list;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
