package com.github.yeriomin.yalpstore;

import android.app.Dialog;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.yeriomin.yalpstore.model.App;
import com.github.yeriomin.yalpstore.model.Review;

import java.util.ArrayList;
import java.util.List;

public class ReviewManager extends DetailsManager {

    static private final int REVIEW_SHOW_COUNT = 3;
    static private final int REVIEW_LOAD_COUNT = 15;
    static private int[] starIds = new int[] { R.id.user_star1, R.id.user_star2, R.id.user_star3, R.id.user_star4, R.id.user_star5 };
    static private int[] averageStarIds = new int[] { R.id.average_stars1, R.id.average_stars2, R.id.average_stars3, R.id.average_stars4, R.id.average_stars5 };
    static private int colorDefault;

    private int reviewShowPage = 0;
    private int reviewLoadPage = 0;
    private boolean allReviewsLoaded;
    private List<Review> reviews = new ArrayList<>();

    public ReviewManager(DetailsActivity activity, App app) {
        super(activity, app);
        colorDefault = ((TextView) activity.findViewById(starIds[0])).getCurrentTextColor();
    }

    public App getApp() {
        return app;
    }

    @Override
    public void draw() {
        activity.initExpandableGroup(R.id.reviews_header, R.id.reviews_container, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showReviews((LinearLayout) activity.findViewById(R.id.reviews_list), app.getPackageName());
            }
        });
        initReviewListControls();
        setText(R.id.average_rating, R.string.details_rating, app.getRating().getAverage());
        for (int starNum = 1; starNum <= 5; starNum++) {
            setText(averageStarIds[starNum - 1], R.string.details_rating_specific, starNum, app.getRating().getStars(starNum));
            final int currentStars = starNum;
            activity.findViewById(starIds[starNum - 1]).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showUserReviewCommentDialog(getUpdatedUserReview(app.getUserReview(), currentStars));
                }
            });
        }

        activity.findViewById(R.id.user_review_container).setVisibility(app.isInstalled() ? View.VISIBLE : View.GONE);
        Review review = app.getUserReview();
        initUserReviewControls(app);
        if (null != review) {
            fillUserReview(review);
        }
    }

    public void fillUserReview(Review review) {
        clearUserReview();
        for (int starNum = 1; starNum <= 5; starNum++) {
            int starId = starIds[starNum - 1];
            TextView starView = (TextView) activity.findViewById(starId);
            starView.setText(starNum <= review.getRating() ? R.string.star_filled : R.string.star_empty);
            starView.setTextColor(starNum <= review.getRating() ? Color.YELLOW : colorDefault);
        }
        setTextOrHide(R.id.user_comment, review.getComment());
        setTextOrHide(R.id.user_title, review.getTitle());
        setText(R.id.rate, R.string.details_you_rated_this_app);
        activity.findViewById(R.id.user_review_edit_delete).setVisibility(View.VISIBLE);
        activity.findViewById(R.id.user_review).setVisibility(View.VISIBLE);
    }

    public void clearUserReview() {
        for (int starId : starIds) {
            TextView starView = (TextView) activity.findViewById(starId);
            starView.setText(R.string.star_empty);
            starView.setTextColor(colorDefault);
        }
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

    private void navigateReviews(View v, String packageName) {
        boolean next = v.getId() == R.id.reviews_next;
        if (next) {
            reviewShowPage++;
        } else {
            reviewShowPage--;
        }
        activity.findViewById(R.id.reviews_previous).setVisibility(
            reviewShowPage > 0
                ? View.VISIBLE
                : View.INVISIBLE
        );
        activity.findViewById(R.id.reviews_next).setVisibility(
            reviews.size() > (reviewShowPage * REVIEW_SHOW_COUNT)
                ? View.VISIBLE
                : View.INVISIBLE
        );
        showReviews((LinearLayout) activity.findViewById(R.id.reviews_list), packageName);
    }

    private void showReviews(LinearLayout list, String packageName) {
        int offset = REVIEW_SHOW_COUNT * reviewShowPage;
        if (reviews.size() > offset) {
            list.removeAllViews();
            for (int i = offset; i < Math.min(REVIEW_SHOW_COUNT + offset, reviews.size()); i++) {
                addReviewToList(reviews.get(i), list);
            }
        }
        if (!allReviewsLoaded && reviews.size() < REVIEW_SHOW_COUNT + offset) {
            loadMoreReviews(list, packageName);
        }
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

    private void loadMoreReviews(LinearLayout list, String packageName) {
        ReviewLoadTask task = new ReviewLoadTask();
        task.setReviewListView(list);
        task.setPackageName(packageName);
        task.setContext(activity);
        task.prepareDialog(R.string.dialog_message_reviews, R.string.dialog_title_reviews);
        task.execute();
    }

    private void showUserReviewCommentDialog(final Review review) {
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.review_dialog_layout);

        final EditText editComment = (EditText) dialog.findViewById(R.id.review_dialog_review_comment);
        editComment.setText(review.getComment());
        final EditText editTitle = (EditText) dialog.findViewById(R.id.review_dialog_review_title);
        editTitle.setText(review.getTitle());

        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.setTitle(R.string.details_review_dialog_title);
        dialog.findViewById(R.id.review_dialog_done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReviewAddTask task = new ReviewAddTask(v.getContext(), ReviewManager.this);
                review.setComment(editComment.getText().toString());
                review.setTitle(editTitle.getText().toString());
                task.execute(review);
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.review_dialog_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void initReviewListControls() {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateReviews(v, app.getPackageName());
            }
        };
        activity.findViewById(R.id.reviews_previous).setOnClickListener(listener);
        activity.findViewById(R.id.reviews_next).setOnClickListener(listener);
    }

    private void initUserReviewControls(final App app) {
        activity.findViewById(R.id.user_review_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUserReviewCommentDialog(app.getUserReview());
            }
        });
        activity.findViewById(R.id.user_review_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReviewDeleteTask task = new ReviewDeleteTask(v.getContext(), ReviewManager.this);
                task.execute(app.getPackageName());
            }
        });
    }

    private void setTextOrHide(int viewId, String text) {
        TextView textView = (TextView) activity.findViewById(viewId);
        if (null != text && !text.isEmpty()) {
            textView.setText(text);
            textView.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.GONE);
        }
    }

    private void setText(int viewId, String text) {
        ((TextView) activity.findViewById(viewId)).setText(text);
    }

    private void setText(int viewId, int stringId, Object... text) {
        setText(viewId, activity.getString(stringId, text));
    }

    private class ReviewLoadTask extends GoogleApiAsyncTask {

        private LinearLayout reviewListView;
        private String packageName;

        public void setPackageName(String packageName) {
            this.packageName = packageName;
        }

        public void setReviewListView(LinearLayout reviewListView) {
            this.reviewListView = reviewListView;
        }

        @Override
        protected Throwable doInBackground(String... params) {
            PlayStoreApiWrapper wrapper = new PlayStoreApiWrapper(activity);
            try {
                if (reviews.addAll(wrapper.getReviews(packageName, ReviewManager.REVIEW_LOAD_COUNT * reviewLoadPage, ReviewManager.REVIEW_LOAD_COUNT))) {
                    reviewLoadPage++;
                } else {
                    allReviewsLoaded = true;
                }
            } catch (Throwable e) {
                return e;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Throwable e) {
            super.onPostExecute(e);
            if (e == null) {
                showReviews(reviewListView, packageName);
                activity.findViewById(R.id.reviews_next).setVisibility(
                    reviews.size() > (reviewShowPage * ReviewManager.REVIEW_SHOW_COUNT)
                        ? View.VISIBLE
                        : View.INVISIBLE
                );
            } else {
                Log.e(DetailsActivity.class.getName(), "Could not get reviews: " + e.getMessage());
            }
        }
    }
}
