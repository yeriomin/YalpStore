/*
 * Yalp Store
 * Copyright (C) 2018 Sergey Yeriomin <yeriomin@gmail.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.github.yeriomin.yalpstore.task.playstore;

import android.util.Log;

import com.github.yeriomin.playstoreapi.GooglePlayAPI;
import com.github.yeriomin.yalpstore.DetailsActivity;
import com.github.yeriomin.yalpstore.ReviewStorageIterator;
import com.github.yeriomin.yalpstore.model.Review;

import java.io.IOException;
import java.util.List;

public class ReviewLoadTask extends PlayStorePayloadTask<List<Review>> {

    private ReviewStorageIterator iterator;
    private com.github.yeriomin.yalpstore.fragment.details.Review fragment;
    private boolean next;

    public void setIterator(ReviewStorageIterator iterator) {
        this.iterator = iterator;
    }

    public void setFragment(com.github.yeriomin.yalpstore.fragment.details.Review fragment) {
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