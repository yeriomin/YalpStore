package in.dragons.galaxy.builders;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.preference.PreferenceManager;

import java.io.IOException;

import in.dragons.galaxy.PlayStoreApiAuthenticator;
import in.dragons.galaxy.R;
import in.dragons.galaxy.task.playstore.PlayStoreTask;

public class AccountTypeDialogBuilder extends CredentialsDialogBuilder {

    public AccountTypeDialogBuilder(Context context) {
        super(context);
    }

    @Override
    public Dialog show() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final Dialog alertDialog = builder
                .setTitle(R.string.dialog_account_type_title)
                .setItems(
                        R.array.accountType,
                        (dialog, which) -> {
                            dialog.dismiss();
                            if (which == 0) {
                                showCredentialsDialog();
                            } else {
                                logInWithPredefinedAccount();
                            }
                        }
                )
                .setCancelable(true)
                .create();
        alertDialog.show();
        return alertDialog;
    }

    public Dialog showCredentialsDialog() {
        UserProvidedAccountDialogBuilder builder = new UserProvidedAccountDialogBuilder(this.context);
        builder.setCaller(caller);
        return builder.show();
    }

    public void refreshToken() {
        RefreshTokenTask task = new RefreshTokenTask();
        task.setCaller(caller);
        task.setContext(context);
        task.execute();
    }

    public void logInWithPredefinedAccount() {
        LoginTask task = new LoginTask();
        task.setCaller(caller);
        task.setContext(context);
        task.prepareDialog(R.string.dialog_message_logging_in_predefined, R.string.dialog_title_logging_in);
        task.execute();
    }

    public abstract static class AppProvidedCredentialsTask extends CredentialsDialogBuilder.CheckCredentialsTask {

        abstract protected void payload() throws IOException;

        @Override
        protected CredentialsDialogBuilder getDialogBuilder() {
            return new AccountTypeDialogBuilder(context);
        }

        @Override
        protected Void doInBackground(String[] params) {
            try {
                payload();
            } catch (IOException e) {
                exception = e;
            }
            return null;
        }
    }

    public static class RefreshTokenTask extends AppProvidedCredentialsTask {

        @Override
        public void setCaller(PlayStoreTask caller) {
            super.setCaller(caller);
        }

        @Override
        protected void payload() throws IOException {
            new PlayStoreApiAuthenticator(context).refreshToken();
        }
    }

    private static class LoginTask extends AppProvidedCredentialsTask {

        @Override
        protected void payload() throws IOException {
            new PlayStoreApiAuthenticator(context).login();
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("LOGGED_IN", true).apply();
            PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("DUMMY_ACC", true).apply();
            PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("GOOGLE_ACC", false).apply();
        }
    }
}
