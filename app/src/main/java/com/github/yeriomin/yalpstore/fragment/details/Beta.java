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
import android.widget.EditText;
import android.widget.TextView;

import com.github.yeriomin.playstoreapi.GooglePlayAPI;
import com.github.yeriomin.yalpstore.ContextUtil;
import com.github.yeriomin.yalpstore.DetailsActivity;
import com.github.yeriomin.yalpstore.R;
import com.github.yeriomin.yalpstore.YalpStoreApplication;
import com.github.yeriomin.yalpstore.fragment.Abstract;
import com.github.yeriomin.yalpstore.model.App;
import com.github.yeriomin.yalpstore.task.playstore.BetaToggleTask;
import com.github.yeriomin.yalpstore.task.playstore.PlayStorePayloadTask;
import com.github.yeriomin.yalpstore.widget.ExpansionPanel;

import java.io.IOException;

public class Beta extends Abstract {

    public Beta(DetailsActivity activity, App app) {
        super(activity, app);
    }

    @Override
    public void draw() {
        if (YalpStoreApplication.user.appProvidedEmail()
            && app.isTestingProgramAvailable()
            && app.isTestingProgramOptedIn()
        ) {
            // Auto-leave beta program if current account is built-in.
            // The users expect stable to be default.
            new BetaToggleTask(app).execute();
            return;
        }
        if (!app.isInstalled()
            || !app.isTestingProgramAvailable()
            || YalpStoreApplication.user.appProvidedEmail()
        ) {
            return;
        }
        activity.findViewById(R.id.beta_panel).setVisibility(View.VISIBLE);
        ((ExpansionPanel) activity.findViewById(R.id.beta_panel)).setHeaderText(app.isTestingProgramOptedIn() ? R.string.testing_program_section_opted_in_title : R.string.testing_program_section_opted_out_title);
        setText(R.id.beta_message, app.isTestingProgramOptedIn() ? R.string.testing_program_section_opted_in_message : R.string.testing_program_section_opted_out_message);
        setText(R.id.beta_subscribe_button, app.isTestingProgramOptedIn() ? R.string.testing_program_opt_out : R.string.testing_program_opt_in);
        setText(R.id.beta_email, app.getTestingProgramEmail());
        activity.findViewById(R.id.beta_feedback).setVisibility(app.isTestingProgramOptedIn() ? View.VISIBLE : View.GONE);
        activity.findViewById(R.id.beta_subscribe_button).setOnClickListener(new BetaOnClickListener((TextView) activity.findViewById(R.id.beta_message), app));
        activity.findViewById(R.id.beta_submit_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initBetaTask(new BetaFeedbackSubmitTask()).execute();
            }
        });
        activity.findViewById(R.id.beta_delete_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initBetaTask(new BetaFeedbackDeleteTask()).execute();
            }
        });
        if (null != app.getUserReview() && !TextUtils.isEmpty(app.getUserReview().getComment())) {
            ((EditText) activity.findViewById(R.id.beta_comment)).setText(app.getUserReview().getComment());
            activity.findViewById(R.id.beta_delete_button).setVisibility(View.VISIBLE);
        }
    }

    private BetaFeedbackTask initBetaTask(BetaFeedbackTask task) {
        task.setPackageName(app.getPackageName());
        task.setEditText((EditText) activity.findViewById(R.id.beta_comment));
        task.setDeleteButton(activity.findViewById(R.id.beta_delete_button));
        return task;
    }

    static class BetaOnClickListener implements View.OnClickListener {

        private TextView messageView;
        private App app;

        public BetaOnClickListener(TextView messageView, App app) {
            this.messageView = messageView;
            this.app = app;
        }

        @Override
        public void onClick(View view) {
            view.setEnabled(false);
            messageView.setText(app.isTestingProgramOptedIn() ? R.string.testing_program_section_opted_out_propagating_message : R.string.testing_program_section_opted_in_propagating_message);
            new BetaToggleTask(app).execute();
        }
    }

    static abstract private class BetaFeedbackTask extends PlayStorePayloadTask<Void> {

        protected String packageName;
        protected EditText editText;
        protected View deleteButton;

        public void setPackageName(String packageName) {
            this.packageName = packageName;
        }

        public void setEditText(EditText editText) {
            this.editText = editText;
            setContext(editText.getContext());
        }

        public void setDeleteButton(View deleteButton) {
            this.deleteButton = deleteButton;
        }
    }

    static private class BetaFeedbackSubmitTask extends BetaFeedbackTask {

        @Override
        protected Void getResult(GooglePlayAPI api, String... arguments) throws IOException {
            api.betaFeedback(packageName, editText.getText().toString());
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (success()) {
                ContextUtil.toastShort(context, context.getString(R.string.done));
                deleteButton.setVisibility(View.VISIBLE);
            }
        }
    }

    static private class BetaFeedbackDeleteTask extends BetaFeedbackTask {

        @Override
        protected Void getResult(GooglePlayAPI api, String... arguments) throws IOException {
            api.deleteBetaFeedback(packageName);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (success()) {
                editText.setText("");
                ContextUtil.toastShort(context, context.getString(R.string.done));
                deleteButton.setVisibility(View.GONE);
            }
        }
    }
}
