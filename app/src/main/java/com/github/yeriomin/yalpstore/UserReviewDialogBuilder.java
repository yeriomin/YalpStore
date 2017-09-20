package com.github.yeriomin.yalpstore;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.EditText;

import com.github.yeriomin.yalpstore.fragment.details.Review;
import com.github.yeriomin.yalpstore.task.playstore.ReviewAddTask;

public class UserReviewDialogBuilder {

    private Context context;
    private Review manager;
    private String packageName;

    private Dialog dialog;

    public UserReviewDialogBuilder(Context context, Review manager, String packageName) {
        this.context = context;
        this.manager = manager;
        this.packageName = packageName;
    }

    public Dialog show(final com.github.yeriomin.yalpstore.model.Review review) {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.review_dialog_layout);

        getCommentView().setText(review.getComment());
        getTitleView().setText(review.getTitle());

        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.setTitle(R.string.details_review_dialog_title);
        dialog.findViewById(R.id.review_dialog_done).setOnClickListener(new DoneOnClickListener(review));
        dialog.findViewById(R.id.review_dialog_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
        return dialog;
    }

    private EditText getCommentView() {
        return (EditText) dialog.findViewById(R.id.review_dialog_review_comment);
    }

    private EditText getTitleView() {
        return (EditText) dialog.findViewById(R.id.review_dialog_review_title);
    }

    private class DoneOnClickListener implements View.OnClickListener {

        private final com.github.yeriomin.yalpstore.model.Review review;

        public DoneOnClickListener(com.github.yeriomin.yalpstore.model.Review review) {
            this.review = review;
        }

        @Override
        public void onClick(View v) {
            ReviewAddTask task = new ReviewAddTask();
            task.setContext(v.getContext());
            task.setPackageName(packageName);
            task.setFragment(manager);
            review.setComment(getCommentView().getText().toString());
            review.setTitle(getTitleView().getText().toString());
            task.setReview(review);
            task.execute();
            dialog.dismiss();
        }
    }
}
