package com.github.yeriomin.yalpstore;

import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.github.yeriomin.yalpstore.model.App;
import com.github.yeriomin.yalpstore.model.Review;

import java.util.List;

public class ReviewFragment extends DetailsFragment {

    static private int[] averageStarIds = new int[] { R.id.average_stars1, R.id.average_stars2, R.id.average_stars3, R.id.average_stars4, R.id.average_stars5 };

    private ReviewStorageIterator iterator;

    public ReviewFragment(DetailsActivity activity, App app) {
        super(activity, app);
        iterator = new ReviewStorageIterator();
        iterator.setPackageName(app.getPackageName());
        iterator.setContext(activity);
    }

    @Override
    public void draw() {
        initExpandableGroup(R.id.reviews_header, R.id.reviews_container, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTask(true).execute();
            }
        });
        initReviewListControls();

        setText(R.id.average_rating, R.string.details_rating, app.getRating().getAverage());
        for (int starNum = 1; starNum <= 5; starNum++) {
            setText(averageStarIds[starNum - 1], R.string.details_rating_specific, starNum, app.getRating().getStars(starNum));
        }

        activity.findViewById(R.id.user_review_container).setVisibility(isReviewable(app) ? View.VISIBLE : View.GONE);
        Review review = app.getUserReview();
        initUserReviewControls(app);
        if (null != review) {
            fillUserReview(review);
        }
    }

    private boolean isReviewable(App app) {
        return app.isInstalled() && !PreferenceManager.getDefaultSharedPreferences(activity)
            .getString(PreferenceActivity.PREFERENCE_EMAIL, "")
            .equals(AccountTypeDialogBuilder.APP_PROVIDED_EMAIL)
            ;
    }

    public void fillUserReview(Review review) {
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

    private Review getUpdatedUserReview(Review oldReview, int stars) {
        Review review = new Review();
        review.setRating(stars);
        if (null != oldReview) {
            review.setComment(oldReview.getComment());
            review.setTitle(oldReview.getTitle());
        }
        return review;
    }

    public void showReviews(List<Review> reviews) {
        activity.findViewById(R.id.reviews_previous).setVisibility(iterator.hasPrevious() ? View.VISIBLE : View.INVISIBLE);
        activity.findViewById(R.id.reviews_next).setVisibility(iterator.hasNext() ? View.VISIBLE : View.INVISIBLE);
        LinearLayout listView = (LinearLayout) activity.findViewById(R.id.reviews_list);
        listView.removeAllViews();
        for (Review review: reviews) {
            addReviewToList(review, listView);
        }
    }

    private ReviewLoadTask getTask(boolean next) {
        ReviewLoadTask task = new ReviewLoadTask(iterator, this, next);
        task.setContext(activity);
        task.prepareDialog(R.string.dialog_message_reviews, R.string.dialog_title_reviews);
        return task;
    }

    private void addReviewToList(Review review, ViewGroup parent) {
        LinearLayout reviewLayout = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.review_list_item, null, false);
        ((TextView) reviewLayout.findViewById(R.id.author)).setText(review.getUserName());
        ((TextView) reviewLayout.findViewById(R.id.title)).setText(
            activity.getString(R.string.details_rating, (double) review.getRating())
                + " " + review.getTitle()
        );
        ((TextView) reviewLayout.findViewById(R.id.comment)).setText(review.getComment());
        parent.addView(reviewLayout);
        ImageDownloadTask task = new ImageDownloadTask();
        task.setView((ImageView) reviewLayout.findViewById(R.id.avatar));
        task.execute((String) review.getUserPhotoUrl());
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
                new UserReviewDialogBuilder(activity, ReviewFragment.this, app.getPackageName())
                    .show(getUpdatedUserReview(app.getUserReview(), (int) rating));
            }
        });
        activity.findViewById(R.id.user_review_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new UserReviewDialogBuilder(activity, ReviewFragment.this, app.getPackageName())
                    .show(app.getUserReview());
            }
        });
        activity.findViewById(R.id.user_review_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReviewDeleteTask task = new ReviewDeleteTask(v.getContext(), ReviewFragment.this);
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
