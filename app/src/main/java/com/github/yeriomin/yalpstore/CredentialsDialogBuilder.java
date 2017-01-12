package com.github.yeriomin.yalpstore;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.github.yeriomin.playstoreapi.AuthException;

import java.io.IOException;

public class CredentialsDialogBuilder {

    private Context context;
    protected GoogleApiAsyncTask taskClone;

    public CredentialsDialogBuilder(Context context) {
        this.context = context;
    }

    public void setTaskClone(GoogleApiAsyncTask taskClone) {
        this.taskClone = taskClone;
    }

    public Dialog show() {
        final Dialog ad = new Dialog(context);
        ad.setContentView(R.layout.credentials_dialog_layout);
        ad.setTitle(context.getString(R.string.credentials_title));
        ad.setCancelable(false);

        final EditText editEmail = (EditText) ad.findViewById(R.id.email);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        editEmail.setText(prefs.getString(PreferenceActivity.PREFERENCE_EMAIL, ""));
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
                task.setTaskClone(taskClone);
                task.setDialog(ad);
                task.execute(email, password);
            }
        });

        ad.show();
        return ad;
    }

    private class CheckCredentialsTask extends AsyncTask<String, Void, Throwable> {

        private Dialog dialog;
        private RelativeLayout progressOverlay;
        protected GoogleApiAsyncTask taskClone;

        public void setTaskClone(GoogleApiAsyncTask taskClone) {
            this.taskClone = taskClone;
        }

        public void setDialog(Dialog dialog) {
            this.dialog = dialog;
        }

        @Override
        protected Throwable doInBackground(String[] params) {
            if (params.length < 2
                || params[0] == null
                || params[1] == null
                || params[0].isEmpty()
                || params[1].isEmpty()
                ) {
                Log.w(getClass().getName(), "Email - password pair expected");
            }
            try {
                PlayStoreApiWrapper wrapper = new PlayStoreApiWrapper(this.dialog.getContext());
                wrapper.login(params[0], params[1]);
            } catch (Throwable e) {
                return e;
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            this.progressOverlay = (RelativeLayout) this.dialog.findViewById(R.id.loading);
            this.progressOverlay.setVisibility(View.VISIBLE);
            this.progressOverlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    return;
                }
            });
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Throwable e) {
            super.onPostExecute(e);
            this.progressOverlay.setVisibility(View.GONE);
            Context c = this.dialog.getContext();
            if (null != e) {
                if (e instanceof CredentialsEmptyException) {
                    Log.w(getClass().getName(), "Credentials empty");
                } else if (e instanceof AuthException) {
                    Toast.makeText(
                        c.getApplicationContext(),
                        c.getString(R.string.error_incorrect_password),
                        Toast.LENGTH_LONG
                    ).show();
                } else if (e instanceof IOException) {
                    Toast.makeText(
                        c.getApplicationContext(),
                        c.getString(R.string.error_network_other, e.getMessage()),
                        Toast.LENGTH_LONG
                    ).show();
                } else {
                    Log.w(getClass().getName(), "Unknown exception " + e.getClass().getName() + " " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                this.dialog.dismiss();
                this.taskClone.execute();
            }
        }
    }
}
