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

import java.util.Collection;
import java.util.HashSet;

public class SharedPreferencesCachedSet extends HashSet<String> {

    static private final long DEFAULT_VALID_TIME = 1000*60*60*24;

    static private final String PREFERENCE_PREFIX = "PREFERENCE_";
    static private final String _UPDATE_TIME = "_UPDATE_TIME";
    static private final String _NOT_LOGGED_IN = "_NOT_LOGGED_IN";

    private String name;
    private SharedPreferences preferences;

    public SharedPreferencesCachedSet(String name, Context context) {
        this(name, PreferenceUtil.getDefaultSharedPreferences(context));
    }

    public SharedPreferencesCachedSet(String name, SharedPreferences preferences) {
        this(name);
        setPreferences(preferences);
    }

    public SharedPreferencesCachedSet(String name) {
        this.name = name;
    }

    public void setPreferences(SharedPreferences preferences) {
        this.preferences = preferences;
        addAll(PreferenceUtil.getStringSet(preferences, getStorageKey()));
    }

    public boolean timeToUpdate() {
        return preferences.getLong(getLastUpdateTimeKey(), 0) + getValidTime() < System.currentTimeMillis();
    }

    @Override
    public boolean addAll(Collection<? extends String> c) {
        boolean modified = super.addAll(c);
        if (modified) {
            save();
        }
        return modified;
    }

    @Override
    public void clear() {
        super.clear();
        save();
    }

    public void save() {
        PreferenceUtil.putStringSet(preferences, getStorageKey(), this);
        preferences.edit().putLong(getLastUpdateTimeKey(), System.currentTimeMillis()).commit();
    }

    protected String getStorageKey() {
        return PREFERENCE_PREFIX + name + (YalpStoreApplication.user.appProvidedEmail() ? _NOT_LOGGED_IN : YalpStoreApplication.user.getEmail());
    }

    protected String getLastUpdateTimeKey() {
        return getStorageKey() + _UPDATE_TIME;
    }

    protected long getValidTime() {
        return DEFAULT_VALID_TIME;
    }
}
