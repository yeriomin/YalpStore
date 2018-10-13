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
import android.content.SharedPreferences;

import java.util.Locale;

public class SharedPreferencesTranslator {

    private static final String PREFIX = "translation";
    private SharedPreferences prefs;

    public SharedPreferencesTranslator(Context context) {
        this.prefs = context.getSharedPreferences(getClass().getName(), Context.MODE_PRIVATE);
    }

    public String getString(String id, Object... params) {
        return String.format(prefs.getString(getFullId(id), id), params);
    }

    public void putString(String id, String value) {
        prefs.edit().putString(getFullId(id), value).commit();
    }

    static private String getFullId(String partId) {
        return PREFIX + "_" + Locale.getDefault().getLanguage() + "_" + partId;
    }
}
