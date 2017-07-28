package com.github.yeriomin.yalpstore;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.github.yeriomin.playstoreapi.AuthException;
import com.github.yeriomin.playstoreapi.GooglePlayException;
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
            super.onPostExecute(e);
            if (null == e) {
                new FirstLaunchChecker(context).setLoggedIn();
                if (null != this.taskClone) {
                    this.taskClone.execute();
                } else {
                    Log.i(getClass().getName(), "No task clone provided");
                }
            }
        }

        @Override
        protected void processException(Throwable e) {
            super.processException(e);
            if ((e instanceof GooglePlayException && ((GooglePlayException) e).getCode() == 500)
                || (e instanceof AuthException && !TextUtils.isEmpty(((AuthException) e).getTwoFactorUrl()))
            ) {
                return;
            }
            CredentialsDialogBuilder builder = getDialogBuilder();
            if (null != this.taskClone) {
                builder.setTaskClone(this.taskClone);
            }
            builder.show();
        }

        @Override
        protected void processIOException(IOException e) {
            super.processIOException(e);
            if (e instanceof TokenDispenserException) {
                e.getCause().printStackTrace();
                toast(context, R.string.error_token_dispenser_problem);
            } else if (e instanceof GooglePlayException && ((GooglePlayException) e).getCode() == 500) {
                PreferenceManager.getDefaultSharedPreferences(context).edit().putString(PreferenceActivity.PREFERENCE_BACKGROUND_UPDATE_INTERVAL, "-1").commit();
                toast(context, R.string.error_invalid_device_definition);
                context.startActivity(new Intent(context, PreferenceActivity.class));
            }
        }

        @Override
        protected void processAuthException(AuthException e) {
            if (e instanceof CredentialsEmptyException) {
                Log.w(getClass().getName(), "Credentials empty");
            } else if (null != e.getTwoFactorUrl()) {
                getTwoFactorAuthDialog().show();
            } else {
                toast(context, R.string.error_incorrect_password);
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
