/*
 * Yalp Store
 * Copyright (C) 2018 Sergey Yeriomin <yeriomin@gmail.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

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
