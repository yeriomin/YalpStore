package com.github.yeriomin.yalpstore;

import android.app.Dialog;
import android.content.Context;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.github.yeriomin.playstoreapi.AuthException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class UserProvidedAccountDialogBuilder extends CredentialsDialogBuilder {

    static private final String USED_EMAILS_SET = "USED_EMAILS_SET";

    private String previousEmail = "";

    public UserProvidedAccountDialogBuilder setPreviousEmail(String previousEmail) {
        this.previousEmail = previousEmail;
        return this;
    }

    public UserProvidedAccountDialogBuilder(Context context) {
        super(context);
    }

    @Override
    public Dialog show() {
        final Dialog ad = new Dialog(context);
        ad.setContentView(R.layout.credentials_dialog_layout);
        ad.setTitle(context.getString(R.string.credentials_title));
        ad.setCancelable(false);

        final AutoCompleteTextView editEmail = getEmailInput(ad);
        final EditText editPassword = (EditText) ad.findViewById(R.id.password);

        Button buttonExit = (Button) ad.findViewById(R.id.button_exit);
        buttonExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });

        Button buttonOk = (Button) ad.findViewById(R.id.button_ok);
        buttonOk.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context c = view.getContext();
                String email = editEmail.getText().toString();
                String password = editPassword.getText().toString();
                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    ContextUtil.toast(c.getApplicationContext(), R.string.error_credentials_empty);
                    return;
                }
                ad.dismiss();
                getUserCredentialsTask().execute(email, password);
            }
        });

        ad.show();
        return ad;
    }

    private UserProvidedCredentialsTask getUserCredentialsTask() {
        UserProvidedCredentialsTask task = new UserProvidedCredentialsTask();
        task.setCaller(caller);
        task.setContext(context);
        task.prepareDialog(R.string.dialog_message_logging_in_provided_by_user, R.string.dialog_title_logging_in);
        return task;
    }

    private AutoCompleteTextView getEmailInput(Dialog ad) {
        AutoCompleteTextView editEmail = (AutoCompleteTextView) ad.findViewById(R.id.email);
        editEmail.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, getUsedEmails()));
        editEmail.setText(PreferenceManager.getDefaultSharedPreferences(context).getString(PreferenceActivity.PREFERENCE_EMAIL, this.previousEmail));
        return editEmail;
    }

    private void addUsedEmail(String email) {
        Set<String> emailsSet = Util.getStringSet(context, USED_EMAILS_SET);
        emailsSet.add(email);
        Util.putStringSet(context, USED_EMAILS_SET, emailsSet);
    }

    private List<String> getUsedEmails() {
        List<String> emails = new ArrayList<>(Util.getStringSet(context, USED_EMAILS_SET));
        Collections.sort(emails);
        return emails;
    }

    private class UserProvidedCredentialsTask extends CredentialsDialogBuilder.CheckCredentialsTask {

        private String previousEmail;

        @Override
        protected CredentialsDialogBuilder getDialogBuilder() {
            return new UserProvidedAccountDialogBuilder(context).setPreviousEmail(previousEmail);
        }

        @Override
        protected Void doInBackground(String[] params) {
            if (params.length < 2
                || params[0] == null
                || params[1] == null
                || TextUtils.isEmpty(params[0])
                || TextUtils.isEmpty(params[1])
            ) {
                exception = new CredentialsEmptyException();
                return null;
            }
            previousEmail = params[0];
            try {
                new PlayStoreApiAuthenticator(context).login(params[0], params[1]);
                addUsedEmail(params[0]);
            } catch (Throwable e) {
                if (e instanceof AuthException && null != ((AuthException) e).getTwoFactorUrl()) {
                    addUsedEmail(params[0]);
                }
                exception = e;
            }
            return null;
        }
    }
}
