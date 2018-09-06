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
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.github.yeriomin.playstoreapi.AuthException;
import com.github.yeriomin.playstoreapi.GooglePlayException;
import com.github.yeriomin.playstoreapi.TokenDispenserException;
import com.github.yeriomin.yalpstore.BaseActivity;
import com.github.yeriomin.yalpstore.ContextUtil;
import com.github.yeriomin.yalpstore.CredentialsEmptyException;
import com.github.yeriomin.yalpstore.FirstLaunchChecker;
import com.github.yeriomin.yalpstore.PreferenceActivity;
import com.github.yeriomin.yalpstore.PreferenceUtil;
import com.github.yeriomin.yalpstore.R;
import com.github.yeriomin.yalpstore.YalpStoreActivity;
import com.github.yeriomin.yalpstore.view.CredentialsDialogBuilder;
import com.github.yeriomin.yalpstore.view.DialogWrapper;
import com.github.yeriomin.yalpstore.view.DialogWrapperAbstract;

import java.io.IOException;

public abstract class CheckCredentialsTask extends PlayStoreTask<Void> {

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
                Log.i(getClass().getSimpleName(), caller.getClass().getSimpleName() + " is cloneable. Retrying.");
                ((PlayStoreTask) ((CloneableTask) caller).clone()).execute((Object[]) new String[] {});
            }
            if (context instanceof BaseActivity) {
                ((BaseActivity) context).redrawAccounts();
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
        if (ContextUtil.isAlive(context)) {
            CredentialsDialogBuilder builder = getDialogBuilder();
            if (null != caller) {
                builder.setCaller(caller);
            }
            builder.show();
        }
    }

    @Override
    protected void processIOException(IOException e) {
        super.processIOException(e);
        if (e instanceof TokenDispenserException) {
            ContextUtil.toast(context, R.string.error_token_dispenser_problem);
        } else if (e instanceof GooglePlayException && ((GooglePlayException) e).getCode() == 500) {
            PreferenceUtil.getDefaultSharedPreferences(context).edit().putString(PreferenceUtil.PREFERENCE_BACKGROUND_UPDATE_INTERVAL, "-1").commit();
            ContextUtil.toast(context, R.string.error_invalid_device_definition);
            context.startActivity(new Intent(context, PreferenceActivity.class));
        }
    }

    @Override
    protected void processAuthException(AuthException e) {
        if (e instanceof CredentialsEmptyException) {
            Log.w(getClass().getSimpleName(), "Credentials empty");
        } else if (null != e.getTwoFactorUrl() && context instanceof Activity) {
            getTwoFactorAuthDialog().show();
        } else {
            ContextUtil.toast(context, R.string.error_incorrect_password);
        }
    }

    private DialogWrapperAbstract getTwoFactorAuthDialog() {
        DialogWrapper builder = new DialogWrapper((YalpStoreActivity) context);
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
                        if (i.resolveActivity(context.getPackageManager()) != null) {
                            context.startActivity(i);
                        } else {
                            Log.e(getClass().getSimpleName(), "No application available to handle http links... very strange");
                        }
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
            .create()
        ;
    }
}
