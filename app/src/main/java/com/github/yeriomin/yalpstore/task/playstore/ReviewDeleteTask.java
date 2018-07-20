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
import com.github.yeriomin.yalpstore.fragment.details.Review;

import java.io.IOException;

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
