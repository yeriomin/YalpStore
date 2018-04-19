package com.dragons.aurora;


import java.util.ArrayList;
import java.util.List;

import com.dragons.aurora.model.Review;

public class ReviewStorageIterator extends ReviewIterator {

    static private final int PAGE_SIZE = 3;

    private List<Review> list = new ArrayList<>();
    private ReviewRetrieverIterator iterator;

    private ReviewRetrieverIterator getRetrievingIterator() {
        if (null == iterator) {
            iterator = new ReviewRetrieverIterator();
            iterator.setContext(context);
            iterator.setPackageName(packageName);
        }
        return iterator;
    }

    @Override
    public boolean hasNext() {
        return list.size() > (PAGE_SIZE * page) || getRetrievingIterator().hasNext();
    }

    @Override
    public List<Review> next() {
        page++;
        if (list.size() < (PAGE_SIZE * (page + 1)) && getRetrievingIterator().hasNext()) {
            list.addAll(getRetrievingIterator().next());
        }
        return current();
    }

    public boolean hasPrevious() {
        return page > 0;
    }

    public List<Review> previous() {
        page--;
        return current();
    }

    private List<Review> current() {
        int from = PAGE_SIZE * page;
        int to = from + PAGE_SIZE;
        return (from < 0 || to > list.size()) ? new ArrayList<Review>() : list.subList(from, to);
    }
}
