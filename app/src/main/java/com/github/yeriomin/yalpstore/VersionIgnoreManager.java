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

import java.util.Set;

public class VersionIgnoreManager {

    static private final String PREFERENCE_VERSION_BLACK_LIST = "PREFERENCE_VERSION_BLACK_LIST";

    private SharedPreferences preferences;
    private Set<String> ignoredVersions;

    public VersionIgnoreManager(Context context) {
        preferences = PreferenceUtil.getDefaultSharedPreferences(context);
        ignoredVersions = PreferenceUtil.getStringSet(context, PREFERENCE_VERSION_BLACK_LIST);
    }

    public void add(String packageName, int versionCode) {
        ignoredVersions.add(getKey(packageName, versionCode));
        save();
    }

    public void remove(String packageName, int versionCode) {
        ignoredVersions.remove(getKey(packageName, versionCode));
        save();
    }

    public boolean isUpdatable(String packageName, int versionCode) {
        return !ignoredVersions.contains(getKey(packageName, versionCode));
    }

    private void save() {
        PreferenceUtil.putStringSet(preferences, PREFERENCE_VERSION_BLACK_LIST, ignoredVersions);
    }

    static private String getKey(String packageName, int versionCode) {
        return packageName + "|" + Integer.toString(versionCode);
    }
}
