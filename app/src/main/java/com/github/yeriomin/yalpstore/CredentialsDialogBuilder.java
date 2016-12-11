package com.github.yeriomin.yalpstore;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class CredentialsDialogBuilder {

    private Context context;

    public CredentialsDialogBuilder(Context context) {
        this.context = context;
    }

    public Dialog show() {
        final Dialog ad = new Dialog(context);
        ad.setContentView(R.layout.credentials_dialog_layout);
        ad.setTitle(context.getString(R.string.credentials_title));
        ad.setCancelable(false);

        final EditText editEmail = (EditText) ad.findViewById(R.id.email);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        editEmail.setText(prefs.getString(AppListActivity.PREFERENCE_EMAIL, ""));
        final EditText editPassword = (EditText) ad.findViewById(R.id.password);

        Button buttonExit = (Button) ad.findViewById(R.id.button_exit);
        buttonExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.exit(0);
            }
        });

        Button buttonOk = (Button) ad.findViewById(R.id.button_ok);
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context c = view.getContext();
                final String email = editEmail.getText().toString();
                final String password = editPassword.getText().toString();
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(c, c.getString(R.string.error_credentials_empty), Toast.LENGTH_LONG).show();
                    return;
                }

                CheckCredentialsTask task = new CheckCredentialsTask();
                task.setContext(c);
                try {
                    Throwable result = task.execute(email, password).get();
                    if (null == result) {
                        ad.dismiss();
                    }
                } catch (InterruptedException | ExecutionException e) {
                    System.out.println(e.getClass().getName());
                }
            }
        });

        ad.show();
        return ad;
    }

    private class CheckCredentialsTask extends AsyncTask<String, Void, Throwable> {

        private Context context;
        private ProgressDialog progressDialog;

        public void setContext(Context context) {
            this.context = context;
        }

        @Override
        protected Throwable doInBackground(String[] params) {
            if (params.length < 2
                || params[0] == null
                || params[1] == null
                || params[0].isEmpty()
                || params[1].isEmpty()
                ) {
                System.out.println("Email - password pair expected");
            }
            try {
                PlayStoreApiWrapper wrapper = new PlayStoreApiWrapper(context);
                wrapper.login(params[0], params[1]);
            } catch (Throwable e) {
                return e;
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.progressDialog = new ProgressDialog(this.context.getApplicationContext());
            this.progressDialog.setTitle(this.context.getString(R.string.credentials_title_logging_in));
            this.progressDialog.setMessage(this.context.getString(R.string.credentials_message_logging_in));
            this.progressDialog.show();
        }

        @Override
        protected void onPostExecute(Throwable e) {
            super.onPostExecute(e);
            this.progressDialog.dismiss();
            if (null != e) {
                if (e instanceof CredentialsRejectedException) {
                    Toast.makeText(
                        this.context.getApplicationContext(),
                        this.context.getString(R.string.error_incorrect_password),
                        Toast.LENGTH_LONG
                    ).show();
                } else if (e instanceof CredentialsEmptyException) {
                    System.out.println("Credentials empty");
                } else if (e instanceof IOException) {
                    Toast.makeText(
                        this.context.getApplicationContext(),
                        this.context.getString(R.string.error_network_other, e.getMessage()),
                        Toast.LENGTH_LONG
                    ).show();
                } else {
                    System.out.println("Unknown exception " + e.getClass().getName() + " " + e.getMessage());
                }
            }
        }
    }
}
