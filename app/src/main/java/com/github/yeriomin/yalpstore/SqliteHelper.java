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

package com.github.yeriomin.yalpstore;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.github.yeriomin.yalpstore.model.EventDao;
import com.github.yeriomin.yalpstore.model.LoginInfo;
import com.github.yeriomin.yalpstore.model.LoginInfoDao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.yeriomin.yalpstore.PlayStoreApiAuthenticator.PREFERENCE_USER_ID;

public class SqliteHelper extends SQLiteOpenHelper {

    static private final List<String> onCreateQueries = new ArrayList<>();
    static private final Map<Integer, List<String>> onUpgradeQueries = new HashMap<>();

    static {
        onCreateQueries.addAll(EventDao.onCreateQueries);
        onCreateQueries.addAll(LoginInfoDao.onCreateQueries);
    }

    private Context context;

    public SqliteHelper(Context context) {
        super(context, BuildConfig.APPLICATION_ID, null, BuildConfig.VERSION_CODE);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            execSQL(db, onCreateQueries);
            LoginInfo loginInfo = PreferenceUtil.getLegacyLoginInfo(context);
            if (loginInfo.isLoggedIn()) {
                new LoginInfoDao(db).insert(loginInfo);
                YalpStoreApplication.user = loginInfo;
                PreferenceUtil.getDefaultSharedPreferences(context).edit().putInt(PREFERENCE_USER_ID, YalpStoreApplication.user.hashCode()).commit();
            }
        } catch (SQLException e) {
            Log.e(getClass().getSimpleName(), "Could not create db: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (int versionCode = oldVersion + 1; versionCode <= newVersion; versionCode++) {
            if (onUpgradeQueries.containsKey(versionCode) && !onUpgradeQueries.get(versionCode).isEmpty()) {
                try {
                    execSQL(db, onUpgradeQueries.get(versionCode));
                } catch (SQLException e) {
                    Log.e(getClass().getSimpleName(), "Could not upgrade db from version " + (versionCode - 1) + " to version " + versionCode + ": " + e.getMessage());
                    throw e;
                }
            }
        }
    }

    private void execSQL(SQLiteDatabase db, List<String> queries) {
        db.beginTransaction();
        try {
            for (String query: queries) {
                db.execSQL(query);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }
}
