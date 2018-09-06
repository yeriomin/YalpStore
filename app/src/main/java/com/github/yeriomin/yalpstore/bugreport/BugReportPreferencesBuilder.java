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

package com.github.yeriomin.yalpstore.bugreport;

import android.content.Context;

import com.github.yeriomin.yalpstore.PreferenceUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class BugReportPreferencesBuilder extends BugReportPropertiesBuilder {

    static private final String[] PREFERENCES = {
        PreferenceUtil.PREFERENCE_DOWNLOAD_DELTAS,
        PreferenceUtil.PREFERENCE_AUTO_INSTALL,
        PreferenceUtil.PREFERENCE_UPDATE_LIST_WHITE_OR_BLACK,
        PreferenceUtil.PREFERENCE_UI_THEME,
        PreferenceUtil.PREFERENCE_BACKGROUND_UPDATE_INTERVAL,
        PreferenceUtil.PREFERENCE_DELETE_APK_AFTER_INSTALL,
        PreferenceUtil.PREFERENCE_BACKGROUND_UPDATE_DOWNLOAD,
        PreferenceUtil.PREFERENCE_BACKGROUND_UPDATE_WIFI_ONLY,
        PreferenceUtil.PREFERENCE_BACKGROUND_UPDATE_INSTALL,
        PreferenceUtil.PREFERENCE_INSTALLATION_METHOD,
        PreferenceUtil.PREFERENCE_NO_IMAGES,
        PreferenceUtil.PREFERENCE_DOWNLOAD_INTERNAL_STORAGE,
    };

    public BugReportPreferencesBuilder(Context context) {
        super(context);
        setFileName("preferences.txt");
    }

    @Override
    public BugReportBuilder build() {
        Map<String, ?> prefs = PreferenceUtil.getDefaultSharedPreferences(context).getAll();
        Set<String> whitelist = new HashSet<>(Arrays.asList(PREFERENCES));
        Map<String, String> filtered = new HashMap<>();
        for (String key: prefs.keySet()) {
            if (!whitelist.contains(key)) {
                continue;
            }
            filtered.put(key, String.valueOf(prefs.get(key)));
        }
        setContent(buildProperties(filtered));
        super.build();
        return this;
    }
}
