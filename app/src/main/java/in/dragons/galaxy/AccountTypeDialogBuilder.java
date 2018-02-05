package in.dragons.galaxy;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

import in.dragons.galaxy.task.playstore.PlayStoreTask;

import java.io.IOException;

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
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (which == 0) {
                            showCredentialsDialog();
                        } else {
                            logInWithPredefinedAccount();
                        }
                    }
                }
            )
            .setCancelable(true)
            .create();
        alertDialog.show();
        return alertDialog;
    }

    private Dialog showCredentialsDialog() {
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

    abstract private static class AppProvidedCredentialsTask extends CredentialsDialogBuilder.CheckCredentialsTask {

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

    private static class RefreshTokenTask extends AppProvidedCredentialsTask {

        @Override
        public void setCaller(PlayStoreTask caller) {
            super.setCaller(caller);
            setProgressIndicator(caller.getProgressIndicator());
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
    }
}
