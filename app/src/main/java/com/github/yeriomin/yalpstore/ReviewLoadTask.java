package com.github.yeriomin.yalpstore;

import android.util.Log;

import com.github.yeriomin.yalpstore.model.Review;

import java.util.List;

public class ReviewLoadTask extends GoogleApiAsyncTask {

    private List<Review> list;
    private ReviewStorageIterator iterator;
    private ReviewFragment manager;
    private boolean next;

    public ReviewLoadTask(ReviewStorageIterator iterator, ReviewFragment manager, boolean next) {
        this.iterator = iterator;
        this.manager = manager;
        this.next = next;
    }

    public List<Review> getList() {
        return list;
    }

    @Override
    protected Throwable doInBackground(String... params) {
        try {
            list = next ? iterator.next() : iterator.previous();
        } catch (Throwable e) {
            return e;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Throwable e) {
        super.onPostExecute(e);
        if (e == null) {
            manager.showReviews(list);
        } else {
            Log.e(DetailsActivity.class.getName(), "Could not get reviews: " + e.getMessage());
        }
    }
}