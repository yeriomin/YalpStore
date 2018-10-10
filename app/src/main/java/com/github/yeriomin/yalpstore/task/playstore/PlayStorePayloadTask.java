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
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.github.yeriomin.playstoreapi.GooglePlayAPI;
import com.github.yeriomin.playstoreapi.IteratorGooglePlayException;
import com.github.yeriomin.yalpstore.BaseActivity;
import com.github.yeriomin.yalpstore.ContextUtil;
import com.github.yeriomin.yalpstore.PlayStoreApiAuthenticator;
import com.github.yeriomin.yalpstore.PreferenceUtil;
import com.github.yeriomin.yalpstore.SqliteHelper;
import com.github.yeriomin.yalpstore.YalpStoreApplication;
import com.github.yeriomin.yalpstore.model.LoginInfo;
import com.github.yeriomin.yalpstore.model.LoginInfoDao;

import java.io.IOException;
import java.util.List;

import static com.github.yeriomin.yalpstore.PlayStoreApiAuthenticator.PREFERENCE_USER_ID;

abstract public class PlayStorePayloadTask<T> extends PlayStoreTask<T> {

    abstract protected T getResult(GooglePlayAPI api, String... arguments) throws IOException;

    @Override
    protected T doInBackground(String... arguments) {
        if (!YalpStoreApplication.user.isLoggedIn()) {
            selectExistingBuiltInAccount();
        }
        try {
            return getResult(new PlayStoreApiAuthenticator(context).getApi(), arguments);
        } catch (IOException e) {
            exception = e;
        } catch (IteratorGooglePlayException e) {
            exception = e.getCause();
        }
        return null;
    }

    private void selectExistingBuiltInAccount() {
        for (LoginInfo info: getUsers()) {
            if (info.appProvidedEmail() && TextUtils.isEmpty(info.getDeviceDefinitionName())) {
                YalpStoreApplication.user = info;
                PreferenceUtil.getDefaultSharedPreferences(context).edit().putInt(PREFERENCE_USER_ID, YalpStoreApplication.user.hashCode()).commit();
                Activity activity = ContextUtil.getActivity(context);
                if (activity instanceof BaseActivity) {
                    ((BaseActivity) activity).redrawAccounts();
                }
                break;
            }
        }
    }

    private List<LoginInfo> getUsers() {
        SQLiteDatabase db = new SqliteHelper(context).getReadableDatabase();
        List<LoginInfo> users = new LoginInfoDao(db).getAll();
        db.close();
        return users;
    }
}
