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

package com.github.yeriomin.yalpstore.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE;

public class LoginInfoDao extends Dao {

    private static final String TABLE = "loginInfo";

    private static final String KEY_ID = "_id";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_PIC_URL = "userPicUrl";
    private static final String KEY_LOCALE = "locale";
    private static final String KEY_GSF_ID = "gsfId";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_TOKEN_DISPENSER_URL = "tokenDispenserUrl";
    private static final String KEY_DEVICE_DEFINITION_NAME = "deviceDefinitionName";
    private static final String KEY_DEVICE_DEFINITION_DISPLAY_NAME = "deviceDefinitionDisplayName";
    private static final String KEY_DEVICE_CHECKIN_CONSISTENCY_TOKEN = "deviceCheckinConsistencyToken";
    private static final String KEY_DEVICE_CONFIG_TOKEN = "deviceConfigToken";
    private static final String KEY_DFE_COOKIE = "dfeCookie";

    public static final List<String> onCreateQueries = new ArrayList<>();

    static {
        onCreateQueries.add("CREATE TABLE " + TABLE + "(" +
            KEY_ID + " INTEGER PRIMARY KEY," +
            KEY_EMAIL + " TEXT," +
            KEY_USER_NAME + " TEXT," +
            KEY_USER_PIC_URL + " TEXT," +
            KEY_LOCALE + " TEXT," +
            KEY_GSF_ID + " TEXT," +
            KEY_TOKEN + " TEXT," +
            KEY_TOKEN_DISPENSER_URL + " TEXT," +
            KEY_DEVICE_DEFINITION_NAME + " TEXT," +
            KEY_DEVICE_DEFINITION_DISPLAY_NAME + " TEXT," +
            KEY_DEVICE_CHECKIN_CONSISTENCY_TOKEN + " TEXT," +
            KEY_DEVICE_CONFIG_TOKEN + " TEXT," +
            KEY_DFE_COOKIE + " TEXT" +
            ")"
        );
    }

    public LoginInfoDao(SQLiteDatabase db) {
        super(db);
    }

    public List<LoginInfo> getAll() {
        Cursor cursor = query();
        Map<String, Integer> columnIndexes = getColumnIndexes(cursor);
        List<LoginInfo> items = new ArrayList<>();
        while (cursor.moveToNext()) {
            items.add(getObject(cursor, columnIndexes));
        }
        cursor.close();
        return items;
    }

    public LoginInfo get(int id) {
        LoginInfo result = null;
        Cursor cursor = query(id);
        if (cursor.moveToNext()) {
            result = getObject(cursor, getColumnIndexes(cursor));
            cursor.close();
        }
        return result;
    }

    public void insert(LoginInfo loginInfo) {
        ContentValues contentValues = new ContentValues();
        Log.e(getClass().getSimpleName(), "Saving " + loginInfo);
        contentValues.put(KEY_ID, loginInfo.hashCode());
        contentValues.put(KEY_EMAIL, loginInfo.getEmail());
        contentValues.put(KEY_USER_NAME, loginInfo.getUserName());
        contentValues.put(KEY_USER_PIC_URL, loginInfo.getUserPicUrl());
        contentValues.put(KEY_LOCALE, loginInfo.getLocaleString());
        contentValues.put(KEY_GSF_ID, loginInfo.getGsfId());
        contentValues.put(KEY_TOKEN, loginInfo.getToken());
        contentValues.put(KEY_TOKEN_DISPENSER_URL, loginInfo.getTokenDispenserUrl());
        contentValues.put(KEY_DEVICE_DEFINITION_NAME, loginInfo.getDeviceDefinitionName());
        contentValues.put(KEY_DEVICE_DEFINITION_DISPLAY_NAME, loginInfo.getDeviceDefinitionDisplayName());
        contentValues.put(KEY_DEVICE_CHECKIN_CONSISTENCY_TOKEN, loginInfo.getDeviceCheckinConsistencyToken());
        contentValues.put(KEY_DEVICE_CONFIG_TOKEN, loginInfo.getDeviceConfigToken());
        contentValues.put(KEY_DFE_COOKIE, loginInfo.getDfeCookie());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            db.insertWithOnConflict(TABLE, null, contentValues, CONFLICT_REPLACE);
        } else {
            delete(loginInfo);
            db.insert(TABLE, null, contentValues);
        }
    }

    public void delete(LoginInfo loginInfo) {
        db.delete(TABLE, KEY_ID + "=?", new String[] {Integer.toString(loginInfo.hashCode())});
    }

    private Cursor query() {
        return query(0);
    }

    private Cursor query(int id) {
        String selection = "";
        String[] selectionArgs = new String[] {};
        if (id > 0) {
            selection = KEY_ID + "=?";
            selectionArgs = new String[] { Integer.toString(id) };
        }
        return db.query(TABLE, null, selection, selectionArgs, null, null, null, id > 0 ? "1" : null);
    }

    private LoginInfo getObject(Cursor cursor, Map<String, Integer> columnIndexes) {
        LoginInfo item = new LoginInfo();
        item.setEmail(cursor.getString(columnIndexes.get(KEY_EMAIL)));
        item.setUserName(cursor.getString(columnIndexes.get(KEY_USER_NAME)));
        item.setUserPicUrl(cursor.getString(columnIndexes.get(KEY_USER_PIC_URL)));
        item.setLocale(cursor.getString(columnIndexes.get(KEY_LOCALE)));
        item.setGsfId(cursor.getString(columnIndexes.get(KEY_GSF_ID)));
        item.setToken(cursor.getString(columnIndexes.get(KEY_TOKEN)));
        item.setTokenDispenserUrl(cursor.getString(columnIndexes.get(KEY_TOKEN_DISPENSER_URL)));
        item.setDeviceDefinitionName(cursor.getString(columnIndexes.get(KEY_DEVICE_DEFINITION_NAME)));
        item.setDeviceDefinitionDisplayName(cursor.getString(columnIndexes.get(KEY_DEVICE_DEFINITION_DISPLAY_NAME)));
        item.setDeviceCheckinConsistencyToken(cursor.getString(columnIndexes.get(KEY_DEVICE_CHECKIN_CONSISTENCY_TOKEN)));
        item.setDeviceConfigToken(cursor.getString(columnIndexes.get(KEY_DEVICE_CONFIG_TOKEN)));
        item.setDfeCookie(cursor.getString(columnIndexes.get(KEY_DFE_COOKIE)));
        return item;
    }

    private Map<String, Integer> getColumnIndexes(Cursor cursor) {
        Map<String, Integer> columnIndexes = new HashMap<>();
        columnIndexes.put(KEY_ID, cursor.getColumnIndex(KEY_ID));
        columnIndexes.put(KEY_EMAIL, cursor.getColumnIndex(KEY_EMAIL));
        columnIndexes.put(KEY_USER_NAME, cursor.getColumnIndex(KEY_USER_NAME));
        columnIndexes.put(KEY_USER_PIC_URL, cursor.getColumnIndex(KEY_USER_PIC_URL));
        columnIndexes.put(KEY_LOCALE, cursor.getColumnIndex(KEY_LOCALE));
        columnIndexes.put(KEY_GSF_ID, cursor.getColumnIndex(KEY_GSF_ID));
        columnIndexes.put(KEY_TOKEN, cursor.getColumnIndex(KEY_TOKEN));
        columnIndexes.put(KEY_TOKEN_DISPENSER_URL, cursor.getColumnIndex(KEY_TOKEN_DISPENSER_URL));
        columnIndexes.put(KEY_DEVICE_DEFINITION_NAME, cursor.getColumnIndex(KEY_DEVICE_DEFINITION_NAME));
        columnIndexes.put(KEY_DEVICE_DEFINITION_DISPLAY_NAME, cursor.getColumnIndex(KEY_DEVICE_DEFINITION_DISPLAY_NAME));
        columnIndexes.put(KEY_DEVICE_CHECKIN_CONSISTENCY_TOKEN, cursor.getColumnIndex(KEY_DEVICE_CHECKIN_CONSISTENCY_TOKEN));
        columnIndexes.put(KEY_DEVICE_CONFIG_TOKEN, cursor.getColumnIndex(KEY_DEVICE_CONFIG_TOKEN));
        columnIndexes.put(KEY_DFE_COOKIE, cursor.getColumnIndex(KEY_DFE_COOKIE));
        return columnIndexes;
    }
}
