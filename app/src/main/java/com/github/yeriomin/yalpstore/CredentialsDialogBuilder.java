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

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import javax.net.ssl.SSLHandshakeException;

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

        @Override
        protected void onPostExecute(Throwable e) {
            if (null != this.progressDialog) {
                this.progressDialog.dismiss();
            }
            if (null != e) {
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
                    if (e instanceof UnknownHostException
                        || e instanceof SSLHandshakeException
                        || e instanceof ConnectException
                        || e instanceof SocketException
                        || e instanceof SocketTimeoutException) {
                        message = this.context.getString(R.string.error_no_network);
                    } else {
                        message = this.context.getString(R.string.error_network_other, e.getClass().getName() + " " + e.getMessage());
                    }
                    toast(context, message);
                } else {
                    Log.w(getClass().getName(), "Unknown exception " + e.getClass().getName() + " " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                this.taskClone.execute();
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
