package com.github.yeriomin.yalpstore;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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

    static private final String APP_PASSWORDS_URL = "https://security.google.com/settings/security/apppasswords";

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
                    toast(c, R.string.error_credentials_empty);
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

    static private void toast(Context c, int stringId, String... stringArgs) {
        Toast.makeText(
            c.getApplicationContext(),
            c.getString(stringId, (Object[]) stringArgs),
            Toast.LENGTH_LONG
        ).show();
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
                public void onClick(View v) {}
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
                    if (null != ((AuthException) e).getTwoFactorUrl()) {
                        this.dialog.dismiss();
                        getTwoFactorAuthDialog().show();
                    } else {
                        toast(c, R.string.error_incorrect_password);
                    }
                } else if (e instanceof IOException) {
                    toast(c, R.string.error_network_other, e.getMessage());
                } else {
                    Log.w(getClass().getName(), "Unknown exception " + e.getClass().getName() + " " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                this.dialog.dismiss();
                this.taskClone.execute();
            }
        }

        private AlertDialog getTwoFactorAuthDialog() {
            final Context context = this.dialog.getContext();
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            return builder
                .setMessage(R.string.dialog_message_two_factor)
                .setTitle(R.string.dialog_title_two_factor)
                .setPositiveButton(
                    R.string.dialog_two_factor_create_password,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(APP_PASSWORDS_URL));
                            context.startActivity(i);
                            System.exit(0);
                        }
                    }
                )
                .setNegativeButton(
                    R.string.dialog_two_factor_cancel,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    }
                )
                .create();
        }
    }
}
