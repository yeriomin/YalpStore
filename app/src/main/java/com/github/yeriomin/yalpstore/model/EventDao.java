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
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventDao extends Dao {

    private static final String TABLE = "events";

    private static final String KEY_TYPE = "type";
    private static final String KEY_PACKAGE_NAME = "packageName";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_TIME = "time";
    private static final String KEY_CHANGES = "changes";

    public static final List<String> onCreateQueries = new ArrayList<>();

    static {
        onCreateQueries.add("CREATE TABLE " + TABLE + "(" +
            KEY_TYPE + " TEXT," +
            KEY_PACKAGE_NAME + " TEXT," +
            KEY_MESSAGE + " TEXT," +
            KEY_TIME + " REAL," +
            KEY_CHANGES + " TEXT" +
            ")"
        );
        onCreateQueries.add("CREATE INDEX idx_" + KEY_TYPE + " ON " + TABLE + " (" + KEY_TYPE + ");");
        onCreateQueries.add("CREATE INDEX idx_" + KEY_PACKAGE_NAME + " ON " + TABLE + " (" + KEY_PACKAGE_NAME + ");");
        onCreateQueries.add("CREATE INDEX idx_" + KEY_TIME + " ON " + TABLE + " (" + KEY_TIME + ");");
    }

    public EventDao(SQLiteDatabase db) {
        super(db);
    }

    public List<Event> getByPackageName(String packageName) {
        Cursor cursor = query(packageName);
        Map<String, Integer> columnIndexes = getColumnIndexes(cursor);
        List<Event> items = new ArrayList<>();
        while (cursor.moveToNext()) {
            items.add(getObject(cursor, columnIndexes));
        }
        cursor.close();
        return items;
    }

    public void insert(Event event) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_TYPE, event.getType().toString());
        contentValues.put(KEY_PACKAGE_NAME, event.getPackageName());
        contentValues.put(KEY_MESSAGE, event.getMessage());
        contentValues.put(KEY_TIME, event.getTime());
        if (event.getType().equals(Event.TYPE.INSTALLATION) || event.getType().equals(Event.TYPE.UPDATE)) {
            contentValues.put(KEY_CHANGES, sameChangesAsPrevious(event.getPackageName(), event.getChanges()) ? "" : event.getChanges());
        }
        db.insert(TABLE, null, contentValues);
    }

    private Cursor query(String packageName) {
        String selection = "";
        String[] selectionArgs = new String[] {};
        if (!TextUtils.isEmpty(packageName)) {
            selection = KEY_PACKAGE_NAME + "=?";
            selectionArgs = new String[] {packageName};
        }
        return db.query(TABLE, null, selection, selectionArgs, null, null, KEY_TIME + " DESC");
    }

    private boolean sameChangesAsPrevious(String packageName, String changes) {
        Cursor cursor = db.query(
            TABLE,
            null,
            KEY_PACKAGE_NAME + "=? AND (" + KEY_TYPE + "=? OR " + KEY_TYPE + "=?) AND LENGTH(" + KEY_CHANGES + ")>0",
            new String[] {packageName, Event.TYPE.INSTALLATION.name(), Event.TYPE.UPDATE.name()},
            null,
            null,
            KEY_TIME + " DESC",
            "1"
        );
        boolean result = false;
        if (cursor.moveToNext()) {
            result = getObject(cursor, getColumnIndexes(cursor)).getChanges().equals(changes);
        }
        cursor.close();
        return result;
    }

    private Event getObject(Cursor cursor, Map<String, Integer> columnIndexes) {
        Event item = new Event();
        item.setType(Event.TYPE.valueOf(cursor.getString(columnIndexes.get(KEY_TYPE))));
        item.setPackageName(cursor.getString(columnIndexes.get(KEY_PACKAGE_NAME)));
        item.setMessage(cursor.getString(columnIndexes.get(KEY_MESSAGE)));
        item.setTime(cursor.getLong(columnIndexes.get(KEY_TIME)));
        item.setChanges(cursor.getString(columnIndexes.get(KEY_CHANGES)));
        return item;
    }

    private Map<String, Integer> getColumnIndexes(Cursor cursor) {
        Map<String, Integer> columnIndexes = new HashMap<>();
        columnIndexes.put(KEY_TYPE, cursor.getColumnIndex(KEY_TYPE));
        columnIndexes.put(KEY_PACKAGE_NAME, cursor.getColumnIndex(KEY_PACKAGE_NAME));
        columnIndexes.put(KEY_MESSAGE, cursor.getColumnIndex(KEY_MESSAGE));
        columnIndexes.put(KEY_TIME, cursor.getColumnIndex(KEY_TIME));
        columnIndexes.put(KEY_CHANGES, cursor.getColumnIndex(KEY_CHANGES));
        return columnIndexes;
    }
}
