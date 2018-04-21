package com.dragons.aurora.fragment.details;

import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.dragons.aurora.fragment.DetailsFragment;
import com.dragons.aurora.playstoreapiv2.GooglePlayAPI;

import java.io.IOException;

import com.dragons.aurora.ContextUtil;
import com.dragons.aurora.PlayStoreApiAuthenticator;
import com.dragons.aurora.R;
import com.dragons.aurora.activities.DetailsActivity;
import com.dragons.aurora.model.App;
import com.dragons.aurora.task.playstore.BetaToggleTask;
import com.dragons.aurora.task.playstore.PlayStorePayloadTask;

public class Beta extends AbstractHelper {

    public Beta(DetailsFragment fragment, App app) {
        super(fragment, app);
    }

    @Override
    public void draw() {
        if (PreferenceManager
                .getDefaultSharedPreferences(fragment.getActivity())
                .getBoolean(PlayStoreApiAuthenticator.PREFERENCE_APP_PROVIDED_EMAIL, false)
                && app.isTestingProgramAvailable()
                && app.isTestingProgramOptedIn()) {
            // Auto-leave beta program if current account is built-in.
            // The users expect stable to be default.
            new BetaToggleTask(app).execute();
            return;
        }

        if (app.isInstalled() || app.isTestingProgramAvailable()
                || !PreferenceManager
                .getDefaultSharedPreferences(fragment.getActivity())
                .getBoolean(PlayStoreApiAuthenticator.PREFERENCE_APP_PROVIDED_EMAIL, false)) {

            setText(R.id.beta_header, app.isTestingProgramOptedIn()
                    ? R.string.testing_program_section_opted_in_title
                    : R.string.testing_program_section_opted_out_title);

            setText(R.id.beta_message, app.isTestingProgramOptedIn()
                    ? R.string.testing_program_section_opted_in_message
                    : R.string.testing_program_section_opted_out_message);

            setText(R.id.beta_subscribe_button, app.isTestingProgramOptedIn()
                    ? R.string.testing_program_opt_out
                    : R.string.testing_program_opt_in);

            setText(R.id.beta_email, app.getTestingProgramEmail());

            fragment.getActivity().findViewById(R.id.beta_card).setVisibility(View.VISIBLE);

            fragment.getActivity().findViewById(R.id.beta_feedback).setVisibility(app.isTestingProgramOptedIn()
                    ? View.VISIBLE
                    : View.GONE);

            fragment.getActivity().findViewById(R.id.beta_subscribe_button)
                    .setOnClickListener(new BetaOnClickListener(fragment.getActivity().findViewById(R.id.beta_message), app));

            fragment.getActivity().findViewById(R.id.beta_submit_button)
                    .setOnClickListener(v -> initBetaTask(new BetaFeedbackSubmitTask()).execute());
            fragment.getActivity().findViewById(R.id.beta_delete_button)
                    .setOnClickListener(v -> initBetaTask(new BetaFeedbackDeleteTask()).execute());

            if (null != app.getUserReview()
                    && !TextUtils.isEmpty(app.getUserReview().getComment())) {
                ((EditText) fragment.getActivity().findViewById(R.id.beta_comment))
                        .setText(app.getUserReview().getComment());
                fragment.getActivity().findViewById(R.id.beta_delete_button).setVisibility(View.VISIBLE);
            }
        }
    }

    private BetaFeedbackTask initBetaTask(BetaFeedbackTask task) {
        task.setPackageName(app.getPackageName());
        task.setEditText((EditText) fragment.getActivity().findViewById(R.id.beta_comment));
        task.setDeleteButton(fragment.getActivity().findViewById(R.id.beta_delete_button));
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
            messageView.setText(app.isTestingProgramOptedIn()
                    ? R.string.testing_program_section_opted_out_propagating_message
                    : R.string.testing_program_section_opted_in_propagating_message);

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