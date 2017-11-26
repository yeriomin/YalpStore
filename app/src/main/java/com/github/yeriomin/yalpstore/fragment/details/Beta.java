package com.github.yeriomin.yalpstore.fragment.details;

import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;

import com.github.yeriomin.yalpstore.DetailsActivity;
import com.github.yeriomin.yalpstore.PlayStoreApiAuthenticator;
import com.github.yeriomin.yalpstore.R;
import com.github.yeriomin.yalpstore.model.App;
import com.github.yeriomin.yalpstore.task.playstore.BetaToggleTask;

public class Beta extends Abstract {

    public Beta(DetailsActivity activity, App app) {
        super(activity, app);
    }

    @Override
    public void draw() {
        if (PreferenceManager.getDefaultSharedPreferences(activity).getBoolean(PlayStoreApiAuthenticator.PREFERENCE_APP_PROVIDED_EMAIL, false)
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
            || PreferenceManager.getDefaultSharedPreferences(activity).getBoolean(PlayStoreApiAuthenticator.PREFERENCE_APP_PROVIDED_EMAIL, false)
        ) {
            return;
        }
        initExpandableGroup(R.id.beta_header, R.id.beta_container);
        setText(R.id.beta_header, app.isTestingProgramOptedIn() ? R.string.testing_program_section_opted_in_title : R.string.testing_program_section_opted_out_title);
        setText(R.id.beta_message, app.isTestingProgramOptedIn() ? R.string.testing_program_section_opted_in_message : R.string.testing_program_section_opted_out_message);
        setText(R.id.beta_button, app.isTestingProgramOptedIn() ? R.string.testing_program_opt_out : R.string.testing_program_opt_in);
        setText(R.id.beta_email, app.getTestingProgramEmail());
        activity.findViewById(R.id.beta_button).setOnClickListener(new BetaOnClickListener((TextView) activity.findViewById(R.id.beta_message), app));
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
}
