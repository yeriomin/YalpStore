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
import com.github.yeriomin.playstoreapi.ReviewResponse;
import com.github.yeriomin.yalpstore.DetailsActivity;
import com.github.yeriomin.yalpstore.model.Review;
import com.github.yeriomin.yalpstore.model.ReviewBuilder;

import java.io.IOException;

public class ReviewAddTask extends PlayStorePayloadTask<Review> {

    private com.github.yeriomin.yalpstore.fragment.details.Review fragment;
    private String packageName;
    private Review review;

    public void setFragment(com.github.yeriomin.yalpstore.fragment.details.Review fragment) {
        this.fragment = fragment;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public void setReview(com.github.yeriomin.yalpstore.model.Review review) {
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
        super.onPostExecute(review);
        if (success()) {
            fragment.fillUserReview(review);
        } else {
            Log.e(DetailsActivity.class.getSimpleName(), "Error adding the review: " + getException().getMessage());
            getException().printStackTrace();
        }
    }
}
