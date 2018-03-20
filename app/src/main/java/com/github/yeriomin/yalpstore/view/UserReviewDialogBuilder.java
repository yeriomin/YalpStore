package com.github.yeriomin.yalpstore.view;

import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.github.yeriomin.yalpstore.R;
import com.github.yeriomin.yalpstore.YalpStoreActivity;
import com.github.yeriomin.yalpstore.fragment.details.Review;
import com.github.yeriomin.yalpstore.task.playstore.ReviewAddTask;

public class UserReviewDialogBuilder extends DialogWrapper {

    private Review manager;
    private String packageName;

    public UserReviewDialogBuilder(YalpStoreActivity activity, Review manager, String packageName) {
        super(activity);
        this.manager = manager;
        this.packageName = packageName;
    }

    public DialogWrapper show(final com.github.yeriomin.yalpstore.model.Review review) {
        setLayout(R.layout.review_dialog_layout);

        getCommentView().setText(review.getComment());
        getTitleView().setText(review.getTitle());

        setCancelable(true);
        setTitle(R.string.details_review_dialog_title);
        setPositiveButton(android.R.string.ok, new DoneOnClickListener(review));
        setNegativeButton(android.R.string.cancel, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });
        show();
        return this;
    }

    private EditText getCommentView() {
        return (EditText) findViewById(R.id.review_dialog_review_comment);
    }

    private EditText getTitleView() {
        return (EditText) findViewById(R.id.review_dialog_review_title);
    }

    private class DoneOnClickListener implements DialogInterface.OnClickListener {

        private final com.github.yeriomin.yalpstore.model.Review review;

        public DoneOnClickListener(com.github.yeriomin.yalpstore.model.Review review) {
            this.review = review;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            ReviewAddTask task = new ReviewAddTask();
            task.setContext(activity);
            task.setPackageName(packageName);
            task.setFragment(manager);
            review.setComment(getCommentView().getText().toString());
            review.setTitle(getTitleView().getText().toString());
            task.setReview(review);
            task.execute();
            dismiss();
        }
    }
}
