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
import android.preference.PreferenceManager;

import java.util.Collection;
import java.util.Set;

public class LocalWishlist {

    static private final String PREFERENCE_WISHLIST = "PREFERENCE_WISHLIST";

    private SharedPreferences preferences;
    private Set<String> packageNames;

    public LocalWishlist(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        packageNames = PreferenceUtil.getStringSet(context, PREFERENCE_WISHLIST);
    }

    private void save() {
        PreferenceUtil.putStringSet(preferences, PREFERENCE_WISHLIST, packageNames);
    }

    public String[] get() {
        return packageNames.toArray(new String[packageNames.size()]);
    }

    public void update(Collection<String> newPackageNames) {
        packageNames.clear();
        packageNames.addAll(newPackageNames);
        save();
    }

    public void add(String packageName) {
        packageNames.add(packageName);
        save();
    }

    public void remove(String packageName) {
        packageNames.remove(packageName);
        save();
    }

    public boolean contains(String packageName) {
        return packageNames.contains(packageName);
    }
}
