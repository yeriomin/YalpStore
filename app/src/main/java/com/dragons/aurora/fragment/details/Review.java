package com.dragons.aurora.fragment.details;

import android.preference.PreferenceManager;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import com.dragons.aurora.CircleTransform;
import com.dragons.aurora.PlayStoreApiAuthenticator;
import com.dragons.aurora.R;
import com.dragons.aurora.ReviewStorageIterator;
import com.dragons.aurora.activities.DetailsActivity;
import com.dragons.aurora.builders.UserReviewDialogBuilder;
import com.dragons.aurora.model.App;
import com.dragons.aurora.task.playstore.ReviewDeleteTask;
import com.dragons.aurora.task.playstore.ReviewLoadTask;

public class Review extends Abstract {

    static private int[] averageStarIds = new int[]{R.id.average_stars1, R.id.average_stars2, R.id.average_stars3, R.id.average_stars4, R.id.average_stars5};

    private ReviewStorageIterator iterator;

    public Review(DetailsActivity activity, App app) {
        super(activity, app);
        iterator = new ReviewStorageIterator();
        iterator.setPackageName(app.getPackageName());
        iterator.setContext(activity);
    }

    @Override
    public void draw() {
        if (!app.isInPlayStore() || app.isEarlyAccess())
            return;
        else
            getTask(true).execute();

        initReviewListControls();

        setText(R.id.average_rating, R.string.details_rating, app.getRating().getAverage());
        for (int starNum = 1; starNum <= 5; starNum++) {
            setText(averageStarIds[starNum - 1], R.string.details_rating_specific, starNum, app.getRating().getStars(starNum));
        }

        activity.findViewById(R.id.user_review_container).setVisibility(isReviewable(app) ? View.VISIBLE : View.GONE);
        com.dragons.aurora.model.Review review = app.getUserReview();
        initUserReviewControls(app);
        if (null != review) {
            fillUserReview(review);
        }
    }

    private boolean isReviewable(App app) {
        return app.isInstalled()
                && !app.isTestingProgramOptedIn()
                && !PreferenceManager.getDefaultSharedPreferences(activity).getBoolean(PlayStoreApiAuthenticator.PREFERENCE_APP_PROVIDED_EMAIL, false)
                ;
    }

    public void fillUserReview(com.dragons.aurora.model.Review review) {
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

    private com.dragons.aurora.model.Review getUpdatedUserReview(com.dragons.aurora.model.Review oldReview, int stars) {
        com.dragons.aurora.model.Review review = new com.dragons.aurora.model.Review();
        review.setRating(stars);
        if (null != oldReview) {
            review.setComment(oldReview.getComment());
            review.setTitle(oldReview.getTitle());
        }
        return review;
    }

    public void showReviews(List<com.dragons.aurora.model.Review> reviews) {
        activity.findViewById(R.id.reviews_previous).setVisibility(iterator.hasPrevious() ? View.VISIBLE : View.INVISIBLE);
        activity.findViewById(R.id.reviews_next).setVisibility(iterator.hasNext() ? View.VISIBLE : View.INVISIBLE);
        LinearLayout listView = (LinearLayout) activity.findViewById(R.id.reviews_list);
        listView.removeAllViews();
        for (com.dragons.aurora.model.Review review : reviews) {
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

    private void addReviewToList(com.dragons.aurora.model.Review review, ViewGroup parent) {
        LinearLayout reviewLayout = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.review_list_item, parent, false);
        ((TextView) reviewLayout.findViewById(R.id.author)).setText(review.getUserName());
        ((TextView) reviewLayout.findViewById(R.id.title)).setText(activity.getString(
                R.string.two_items,
                activity.getString(R.string.details_rating, (double) review.getRating()),
                review.getTitle()
        ));
        ((TextView) reviewLayout.findViewById(R.id.comment)).setText(review.getComment());
        Picasso
                .with(activity)
                .load(review.getUserPhotoUrl())
                .placeholder(R.drawable.ic_user_placeholder)
                .transform(new CircleTransform())
                .into((ImageView) reviewLayout.findViewById(R.id.avatar));

        parent.addView(reviewLayout);
    }

    private void initReviewListControls() {
        View.OnClickListener listener = v -> getTask(v.getId() == R.id.reviews_next).execute();
        activity.findViewById(R.id.reviews_previous).setOnClickListener(listener);
        activity.findViewById(R.id.reviews_next).setOnClickListener(listener);
    }

    private void initUserReviewControls(final App app) {
        ((RatingBar) activity.findViewById(R.id.user_stars)).setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            if (!fromUser) {
                return;
            }
            new UserReviewDialogBuilder(activity, Review.this, app.getPackageName())
                    .show(getUpdatedUserReview(app.getUserReview(), (int) rating));
        });
        activity.findViewById(R.id.user_review_edit).setOnClickListener(v ->
                new UserReviewDialogBuilder(activity, Review.this, app.getPackageName())
                        .show(app.getUserReview()));
        activity.findViewById(R.id.user_review_delete).setOnClickListener(v -> {
            ReviewDeleteTask task = new ReviewDeleteTask();
            task.setFragment(Review.this);
            task.setContext(v.getContext());
            task.execute(app.getPackageName());
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