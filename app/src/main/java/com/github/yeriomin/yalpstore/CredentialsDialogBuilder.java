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

import com.github.yeriomin.playstoreapi.AuthException;
import com.github.yeriomin.playstoreapi.GooglePlayException;
import com.github.yeriomin.playstoreapi.TokenDispenserException;
import com.github.yeriomin.yalpstore.task.playstore.CloneableTask;
import com.github.yeriomin.yalpstore.task.playstore.PlayStoreTask;

import java.io.IOException;

abstract public class CredentialsDialogBuilder {

    protected Context context;
    protected PlayStoreTask caller;

    public CredentialsDialogBuilder(Context context) {
        this.context = context;
    }

    public void setCaller(PlayStoreTask caller) {
        this.caller = caller;
    }

    abstract public Dialog show();

    abstract protected class CheckCredentialsTask extends PlayStoreTask<Void> {

        protected PlayStoreTask caller;

        public void setCaller(PlayStoreTask caller) {
            this.caller = caller;
        }

        static private final String APP_PASSWORDS_URL = "https://security.google.com/settings/security/apppasswords";

        abstract protected CredentialsDialogBuilder getDialogBuilder();

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (success()) {
                new FirstLaunchChecker(context).setLoggedIn();
                if (caller instanceof CloneableTask) {
                    Log.i(getClass().getName(), caller.getClass().getName() + " is cloneable. Retrying.");
                    ((PlayStoreTask) ((CloneableTask) caller).clone()).execute(new String[] {});
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
            if (null != caller) {
                builder.setCaller(caller);
            }
            if (ContextUtil.isAlive(context)) {
                builder.show();
            }
        }

        @Override
        protected void processIOException(IOException e) {
            super.processIOException(e);
            if (e instanceof TokenDispenserException) {
                ContextUtil.toast(context, R.string.error_token_dispenser_problem);
            } else if (e instanceof GooglePlayException && ((GooglePlayException) e).getCode() == 500) {
                PreferenceManager.getDefaultSharedPreferences(context).edit().putString(PreferenceActivity.PREFERENCE_BACKGROUND_UPDATE_INTERVAL, "-1").commit();
                ContextUtil.toast(context, R.string.error_invalid_device_definition);
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
                ContextUtil.toast(context, R.string.error_incorrect_password);
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
