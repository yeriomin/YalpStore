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

import android.preference.Preference;
import android.text.TextUtils;

import java.util.Map;

public abstract class OnListPreferenceChangeListener implements Preference.OnPreferenceChangeListener {

    protected Map<String, String> keyValueMap;
    protected String defaultLabel;

    public void setKeyValueMap(Map<String, String> keyValueMap) {
        this.keyValueMap = keyValueMap;
    }

    public void setDefaultLabel(String defaultLabel) {
        this.defaultLabel = defaultLabel;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        setSummary(preference, newValue);
        return true;
    }

    public void setSummary(Preference preference, Object newValue) {
        preference.setSummary(TextUtils.isEmpty((CharSequence) newValue) ? defaultLabel : keyValueMap.get(newValue));
    }
}
