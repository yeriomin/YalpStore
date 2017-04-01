package com.github.yeriomin.yalpstore;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.github.yeriomin.playstoreapi.AuthException;
import com.github.yeriomin.playstoreapi.TokenDispenserException;

import java.io.IOException;

abstract public class CredentialsDialogBuilder {

    protected Context context;
    protected GoogleApiAsyncTask taskClone;

    public CredentialsDialogBuilder(Context context) {
        this.context = context;
    }

    public void setTaskClone(GoogleApiAsyncTask taskClone) {
        this.taskClone = taskClone;
    }

    abstract public Dialog show();

    static protected void toast(Context c, int stringId, String... stringArgs) {
        Toast.makeText(
            c.getApplicationContext(),
            c.getString(stringId, (Object[]) stringArgs),
            Toast.LENGTH_LONG
        ).show();
    }

    abstract protected class CheckCredentialsTask extends GoogleApiAsyncTask {

        static private final String APP_PASSWORDS_URL = "https://security.google.com/settings/security/apppasswords";

        abstract protected CredentialsDialogBuilder getDialogBuilder();

        @Override
        protected void onPostExecute(Throwable e) {
            if (null != this.progressDialog) {
                this.progressDialog.dismiss();
            }
            if (null != e) {
                handleException(e);
                if (e instanceof AuthException && null != ((AuthException) e).getTwoFactorUrl()) {
                    return;
                }
                CredentialsDialogBuilder builder = getDialogBuilder();
                if (null != this.taskClone) {
                    builder.setTaskClone(this.taskClone);
                }
                builder.show();
            } else {
                if (null != this.taskClone) {
                    this.taskClone.execute();
                } else {
                    Log.i(getClass().getName(), "No task clone provided");
                }
            }
        }

        private void handleException(Throwable e) {
            if (e instanceof CredentialsEmptyException) {
                Log.w(getClass().getName(), "Credentials empty");
            } else if (e instanceof TokenDispenserException) {
                e.getCause().printStackTrace();
                toast(context, R.string.error_token_dispenser_problem);
            } else if (e instanceof AuthException) {
                if (null != ((AuthException) e).getTwoFactorUrl()) {
                    getTwoFactorAuthDialog().show();
                } else {
                    toast(context, R.string.error_incorrect_password);
                }
            } else if (e instanceof IOException) {
                String message;
                if (GoogleApiAsyncTask.noNetwork(e)) {
                    message = this.context.getString(R.string.error_no_network);
                } else {
                    message = this.context.getString(R.string.error_network_other, e.getClass().getName() + " " + e.getMessage());
                }
                toast(context, message);
            } else {
                Log.w(getClass().getName(), "Unknown exception " + e.getClass().getName() + " " + e.getMessage());
                e.printStackTrace();
            }
        }

        private AlertDialog getTwoFactorAuthDialog() {
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
                            android.os.Process.killProcess(android.os.Process.myPid());
                        }
                    }
                )
                .setNegativeButton(
                    R.string.dialog_two_factor_cancel,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            android.os.Process.killProcess(android.os.Process.myPid());
                        }
                    }
                )
                .create();
        }
    }
}
