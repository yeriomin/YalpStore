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

package com.github.yeriomin.yalpstore.task.playstore;

import android.app.Activity;
import android.text.TextUtils;

import com.github.yeriomin.yalpstore.CredentialsEmptyException;
import com.github.yeriomin.yalpstore.PlayStoreApiAuthenticator;
import com.github.yeriomin.yalpstore.PreferenceUtil;
import com.github.yeriomin.yalpstore.YalpStoreApplication;
import com.github.yeriomin.yalpstore.model.LoginInfo;
import com.github.yeriomin.yalpstore.view.CredentialsDialogBuilder;
import com.github.yeriomin.yalpstore.view.LoginDialogBuilder;

import java.util.Set;

public class CheckLoginTask extends CheckCredentialsTask {

    private String previousEmail;
    private LoginInfo loginInfo;

    public void setPreviousEmail(String previousEmail) {
        this.previousEmail = previousEmail;
    }

    public void setLoginInfo(LoginInfo loginInfo) {
        this.loginInfo = loginInfo;
    }

    @Override
    protected CredentialsDialogBuilder getDialogBuilder() {
        LoginDialogBuilder dialogBuilder = new LoginDialogBuilder((Activity) context);
        dialogBuilder.setPreviousEmail(previousEmail);
        return dialogBuilder;
    }

    @Override
    protected Void doInBackground(String... strings) {
        if (!loginInfo.appProvidedEmail() && (TextUtils.isEmpty(loginInfo.getEmail()) || TextUtils.isEmpty(loginInfo.getPassword()))) {
            exception = new CredentialsEmptyException();
            return null;
        }
        try {
            if (!loginInfo.appProvidedEmail()) {
                previousEmail = loginInfo.getEmail();
                addUsedEmail(loginInfo.getEmail());
            }
            new PlayStoreApiAuthenticator(context).login(loginInfo);
        } catch (Throwable e) {
            exception = e;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        if (success()) {
            YalpStoreApplication.user = loginInfo;
        }
        super.onPostExecute(result);
    }

    private void addUsedEmail(String email) {
        Set<String> emailsSet = PreferenceUtil.getStringSet(context, LoginDialogBuilder.USED_EMAILS_SET);
        emailsSet.add(email);
        PreferenceUtil.putStringSet(context, LoginDialogBuilder.USED_EMAILS_SET, emailsSet);
    }
}
