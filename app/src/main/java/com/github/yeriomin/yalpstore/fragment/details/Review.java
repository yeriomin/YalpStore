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

package com.github.yeriomin.yalpstore.fragment.details;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.github.yeriomin.yalpstore.DetailsActivity;
import com.github.yeriomin.yalpstore.R;
import com.github.yeriomin.yalpstore.ReviewStorageIterator;
import com.github.yeriomin.yalpstore.YalpStoreApplication;
import com.github.yeriomin.yalpstore.fragment.Abstract;
import com.github.yeriomin.yalpstore.model.App;
import com.github.yeriomin.yalpstore.model.ImageSource;
import com.github.yeriomin.yalpstore.task.LoadImageTask;
import com.github.yeriomin.yalpstore.task.playstore.ReviewDeleteTask;
import com.github.yeriomin.yalpstore.task.playstore.ReviewLoadTask;
import com.github.yeriomin.yalpstore.view.UriOnClickListener;
import com.github.yeriomin.yalpstore.view.UserReviewDialogBuilder;

import java.util.List;

public class Review extends Abstract {

    static private int[] averageStarIds = new int[] { R.id.average_stars1, R.id.average_stars2, R.id.average_stars3, R.id.average_stars4, R.id.average_stars5 };

    private ReviewStorageIterator iterator;

    public Review(DetailsActivity activity, App app) {
        super(activity, app);
        iterator = new ReviewStorageIterator();
        iterator.setPackageName(app.getPackageName());
        iterator.setContext(activity);
    }

    @Override
    public void draw() {
        if (!app.isInPlayStore() || app.isEarlyAccess()) {
            return;
        }

        activity.findViewById(R.id.reviews_panel).setVisibility(View.VISIBLE);
        activity.findViewById(R.id.reviews_panel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTask(true).execute();
                initReviewListControls();

                setText(R.id.average_rating, R.string.details_rating, app.getRating().getAverage());
                for (int starNum = 1; starNum <= 5; starNum++) {
                    setText(averageStarIds[starNum - 1], R.string.details_rating_specific, starNum, app.getRating().getStars(starNum));
                }

                activity.findViewById(R.id.user_review_container).setVisibility(isReviewable(app) ? View.VISIBLE : View.GONE);
                com.github.yeriomin.yalpstore.model.Review review = app.getUserReview();
                initUserReviewControls(app);
                if (null != review) {
                    fillUserReview(review);
                }
            }
        });
    }

    private boolean isReviewable(App app) {
        return app.isInstalled()
            && !app.isTestingProgramOptedIn()
            && !YalpStoreApplication.user.appProvidedEmail()
        ;
    }

    public void fillUserReview(com.github.yeriomin.yalpstore.model.Review review) {
        clearUserReview();
        app.setUserReview(review);
        ((RatingBar) activity.findViewById(R.id.user_stars)).setRating(review.getRating());
        setTextOrHide(R.id.user_comment, review.getComment());
        setTextOrHide(R.id.user_title, review.getTitle());
        setText(R.id.rate, R.string.details_you_rated_this_app);
        activity.findViewById(R.id.user_review_edit_delete).setVisibility(View.VISIBLE);
        activity.findViewById(R.id.user_review).setVisibility(View.VISIBLE);
    }

    public void clearUserReview() {
        ((RatingBar) activity.findViewById(R.id.user_stars)).setRating(0);
        setText(R.id.user_title, "");
        setText(R.id.user_comment, "");
        setText(R.id.rate, R.string.details_rate_this_app);
        activity.findViewById(R.id.user_review_edit_delete).setVisibility(View.GONE);
        activity.findViewById(R.id.user_review).setVisibility(View.GONE);
    }

    private com.github.yeriomin.yalpstore.model.Review getUpdatedUserReview(com.github.yeriomin.yalpstore.model.Review oldReview, int stars) {
        com.github.yeriomin.yalpstore.model.Review review = new com.github.yeriomin.yalpstore.model.Review();
        review.setRating(stars);
        if (null != oldReview) {
            review.setComment(oldReview.getComment());
            review.setTitle(oldReview.getTitle());
        }
        return review;
    }

    public void showReviews(List<com.github.yeriomin.yalpstore.model.Review> reviews) {
        activity.findViewById(R.id.reviews_previous).setVisibility(iterator.hasPrevious() ? View.VISIBLE : View.INVISIBLE);
        activity.findViewById(R.id.reviews_next).setVisibility(iterator.hasNext() ? View.VISIBLE : View.INVISIBLE);
        LinearLayout listView = (LinearLayout) activity.findViewById(R.id.reviews_list);
        listView.removeAllViews();
        for (com.github.yeriomin.yalpstore.model.Review review: reviews) {
            addReviewToList(review, listView);
        }
    }

    private ReviewLoadTask getTask(boolean next) {
        ReviewLoadTask task = new ReviewLoadTask();
        task.setIterator(iterator);
        task.setFragment(this);
        task.setNext(next);
        task.setContext(activity);
        task.setProgressIndicator(activity.findViewById(R.id.progress));
        return task;
    }

    private void addReviewToList(com.github.yeriomin.yalpstore.model.Review review, ViewGroup parent) {
        LinearLayout reviewLayout = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.review_list_item, parent, false);
        ((TextView) reviewLayout.findViewById(R.id.author)).setText(review.getUserName());
        ((TextView) reviewLayout.findViewById(R.id.title)).setText(activity.getString(
            R.string.two_items,
            activity.getString(R.string.details_rating, (double) review.getRating()),
            review.getTitle()
        ));
        ((TextView) reviewLayout.findViewById(R.id.comment)).setText(review.getComment());
        reviewLayout.setOnClickListener(new UriOnClickListener(activity, review.getGooglePlusUrl()));
        parent.addView(reviewLayout);
        new LoadImageTask((ImageView) reviewLayout.findViewById(R.id.avatar)).setImageSource(new ImageSource(review.getUserPhotoUrl())).executeOnExecutorIfPossible();
    }

    private void initReviewListControls() {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTask(v.getId() == R.id.reviews_next).execute();
            }
        };
        activity.findViewById(R.id.reviews_previous).setOnClickListener(listener);
        activity.findViewById(R.id.reviews_next).setOnClickListener(listener);
    }

    private void initUserReviewControls(final App app) {
        ((RatingBar) activity.findViewById(R.id.user_stars)).setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (!fromUser) {
                    return;
                }
                new UserReviewDialogBuilder(activity, Review.this, app.getPackageName())
                    .show(getUpdatedUserReview(app.getUserReview(), (int) rating));
            }
        });
        activity.findViewById(R.id.user_review_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new UserReviewDialogBuilder(activity, Review.this, app.getPackageName())
                    .show(app.getUserReview());
            }
        });
        activity.findViewById(R.id.user_review_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReviewDeleteTask task = new ReviewDeleteTask();
                task.setFragment(Review.this);
                task.setContext(v.getContext());
                task.execute(app.getPackageName());
            }
        });
    }

    private void setTextOrHide(int viewId, String text) {
        TextView textView = (TextView) activity.findViewById(viewId);
        if (!TextUtils.isEmpty(text)) {
            textView.setText(text);
            textView.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.GONE);
        }
    }
}
