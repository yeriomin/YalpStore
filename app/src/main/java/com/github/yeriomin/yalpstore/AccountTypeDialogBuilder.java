package com.github.yeriomin.yalpstore;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

public class AccountTypeDialogBuilder extends CredentialsDialogBuilder {

    static public final String APP_PROVIDED_EMAIL = "yalp.store.user.one@gmail.com";

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
            .setCancelable(false)
            .create();
        alertDialog.show();
        return alertDialog;
    }

    private Dialog showCredentialsDialog() {
        UserProvidedAccountDialogBuilder builder = new UserProvidedAccountDialogBuilder(this.context);
        builder.setTaskClone(this.taskClone);
        return builder.show();
    }

    public void logInWithPredefinedAccount() {
        AppProvidedCredentialsTask task = new AppProvidedCredentialsTask();
        task.setTaskClone(taskClone);
        task.setContext(context);
        task.prepareDialog(R.string.dialog_message_logging_in_predefined, R.string.dialog_title_logging_in);
        task.execute(APP_PROVIDED_EMAIL);
    }

    private class AppProvidedCredentialsTask extends CredentialsDialogBuilder.CheckCredentialsTask {

        @Override
        protected CredentialsDialogBuilder getDialogBuilder() {
            return new AccountTypeDialogBuilder(context);
        }

        @Override
        protected Throwable doInBackground(String[] params) {
            try {
                new PlayStoreApiAuthenticator(context).login(params[0]);
            } catch (Throwable e) {
                return e;
            }
            return null;
        }
    }
}
