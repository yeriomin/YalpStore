package com.github.yeriomin.yalpstore.view;

import android.app.Activity;
import android.content.DialogInterface;

import com.github.yeriomin.yalpstore.R;
import com.github.yeriomin.yalpstore.task.playstore.LoginTask;

public class AccountTypeDialogBuilder extends CredentialsDialogBuilder {

    public AccountTypeDialogBuilder(Activity activity) {
        super(activity);
    }

    @Override
    public DialogWrapperAbstract show() {
        return new DialogWrapper(activity)
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
            .show()
        ;
    }

    private DialogWrapperAbstract showCredentialsDialog() {
        UserProvidedAccountDialogBuilder builder = new UserProvidedAccountDialogBuilder(activity);
        builder.setCaller(caller);
        return builder.show();
    }

    private void logInWithPredefinedAccount() {
        LoginTask task = new LoginTask();
        task.setCaller(caller);
        task.setContext(activity);
        task.prepareDialog(R.string.dialog_message_logging_in_predefined, R.string.dialog_title_logging_in);
        task.execute();
    }
}
