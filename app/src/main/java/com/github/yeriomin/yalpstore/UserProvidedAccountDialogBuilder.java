package com.github.yeriomin.yalpstore;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class UserProvidedAccountDialogBuilder extends CredentialsDialogBuilder {

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

        final EditText editEmail = (EditText) ad.findViewById(R.id.email);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        editEmail.setText(prefs.getString(PreferenceActivity.PREFERENCE_EMAIL, this.previousEmail));
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
                final String email = editEmail.getText().toString();
                final String password = editPassword.getText().toString();
                if (email.isEmpty() || password.isEmpty()) {
                    toast(c, R.string.error_credentials_empty);
                    return;
                }

                ad.dismiss();
                UserProvidedCredentialsTask task = new UserProvidedCredentialsTask();
                task.setTaskClone(taskClone);
                task.setContext(context);
                task.prepareDialog(R.string.dialog_message_logging_in_provided_by_user, R.string.dialog_title_logging_in);
                task.execute(email, password);
            }
        });

        ad.show();
        return ad;
    }

    private class UserProvidedCredentialsTask extends CredentialsDialogBuilder.CheckCredentialsTask {

        private String previousEmail;

        @Override
        protected CredentialsDialogBuilder getDialogBuilder() {
            return new UserProvidedAccountDialogBuilder(context).setPreviousEmail(previousEmail);
        }

        @Override
        protected Throwable doInBackground(String[] params) {
            if (params.length < 2
                || params[0] == null
                || params[1] == null
                || params[0].isEmpty()
                || params[1].isEmpty()
                ) {
                return new CredentialsEmptyException();
            }
            previousEmail = params[0];
            try {
                PlayStoreApiWrapper wrapper = new PlayStoreApiWrapper(this.context);
                wrapper.login(params[0], params[1]);
            } catch (Throwable e) {
                return e;
            }
            return null;
        }
    }
}
