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

package com.github.yeriomin.yalpstore.fragment.preference;

import android.database.sqlite.SQLiteDatabase;
import android.preference.Preference;
import android.util.Log;

import com.github.yeriomin.yalpstore.OnListPreferenceChangeListener;
import com.github.yeriomin.yalpstore.PlayStoreApiAuthenticator;
import com.github.yeriomin.yalpstore.PreferenceActivity;
import com.github.yeriomin.yalpstore.R;
import com.github.yeriomin.yalpstore.SqliteHelper;
import com.github.yeriomin.yalpstore.Util;
import com.github.yeriomin.yalpstore.YalpStoreApplication;
import com.github.yeriomin.yalpstore.model.LoginInfoDao;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class Language extends List {

    public Language(PreferenceActivity activity) {
        super(activity);
    }

    @Override
    protected OnListPreferenceChangeListener getOnListPreferenceChangeListener() {
        OnListPreferenceChangeListener listener = new OnListPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                boolean result = super.onPreferenceChange(preference, newValue);
                if (YalpStoreApplication.user.isLoggedIn()) {
                    try {
                        new PlayStoreApiAuthenticator(activity).getApi().setLocale(new Locale((String) newValue));
                        YalpStoreApplication.user.setLocale((String) newValue);
                        SQLiteDatabase db = new SqliteHelper(activity).getWritableDatabase();
                        new LoginInfoDao(db).insert(YalpStoreApplication.user);
                        db.close();
                    } catch (Throwable e) {
                        Log.w(getClass().getSimpleName(), "Could not save locale: " + e.getMessage());
                    }
                }
                return result;
            }
        };
        listener.setDefaultLabel(activity.getString(R.string.pref_requested_language_default));
        return listener;
    }

    @Override
    protected Map<String, String> getKeyValueMap() {
        Map<String, String> languages = new HashMap<>();
        for (Locale locale: Locale.getAvailableLocales()) {
            String displayName = locale.getDisplayName();
            displayName = displayName.substring(0, 1).toUpperCase(Locale.getDefault()) + displayName.substring(1);
            languages.put(locale.toString(), displayName);
        }
        languages = Util.sort(languages);
        Util.addToStart(
            (LinkedHashMap<String, String>) languages,
            "",
            activity.getString(R.string.pref_requested_language_default)
        );
        return languages;
    }
}
