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

import com.github.yeriomin.playstoreapi.AuthException;
import com.github.yeriomin.yalpstore.CredentialsEmptyException;
import com.github.yeriomin.yalpstore.PlayStoreApiAuthenticator;
import com.github.yeriomin.yalpstore.PreferenceUtil;
import com.github.yeriomin.yalpstore.view.CredentialsDialogBuilder;
import com.github.yeriomin.yalpstore.view.UserProvidedAccountDialogBuilder;

import java.util.Set;

public class UserProvidedCredentialsTask extends CheckCredentialsTask {

    private String previousEmail;

    @Override
    protected CredentialsDialogBuilder getDialogBuilder() {
        return new UserProvidedAccountDialogBuilder((Activity) context).setPreviousEmail(previousEmail);
    }

    @Override
    protected Void doInBackground(String[] params) {
        if (params.length < 2
            || params[0] == null
            || params[1] == null
            || TextUtils.isEmpty(params[0])
            || TextUtils.isEmpty(params[1])
        ) {
            exception = new CredentialsEmptyException();
            return null;
        }
        previousEmail = params[0];
        try {
            new PlayStoreApiAuthenticator(context).login(params[0], params[1]);
            addUsedEmail(params[0]);
        } catch (Throwable e) {
            if (e instanceof AuthException && null != ((AuthException) e).getTwoFactorUrl()) {
                addUsedEmail(params[0]);
            }
            exception = e;
        }
        return null;
    }

    private void addUsedEmail(String email) {
        Set<String> emailsSet = PreferenceUtil.getStringSet(context, UserProvidedAccountDialogBuilder.USED_EMAILS_SET);
        emailsSet.add(email);
        PreferenceUtil.putStringSet(context, UserProvidedAccountDialogBuilder.USED_EMAILS_SET, emailsSet);
    }
}
