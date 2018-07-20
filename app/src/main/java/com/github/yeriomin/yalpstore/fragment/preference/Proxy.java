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

import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.text.InputType;

import com.github.yeriomin.yalpstore.PreferenceActivity;
import com.github.yeriomin.yalpstore.PreferenceUtil;

class Proxy extends Abstract {


    @Override
    public void draw() {
        ListPreference proxyType = (ListPreference) activity.findPreference(PreferenceUtil.PREFERENCE_PROXY_TYPE);
        proxyType.setOnPreferenceChangeListener(new SummaryOnChangeListener());
        refreshSummary(proxyType);
        EditTextPreference proxyHost = (EditTextPreference) activity.findPreference(PreferenceUtil.PREFERENCE_PROXY_HOST);
        proxyHost.setOnPreferenceChangeListener(new SummaryOnChangeListener());
        refreshSummary(proxyHost);
        EditTextPreference proxyPort = (EditTextPreference) activity.findPreference(PreferenceUtil.PREFERENCE_PROXY_PORT);
        proxyPort.setOnPreferenceChangeListener(new SummaryOnChangeListener());
        proxyPort.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
        refreshSummary(proxyPort);
    }

    protected void refreshSummary(ListPreference preference) {
        preference.getOnPreferenceChangeListener().onPreferenceChange(preference, preference.getValue());
    }

    protected void refreshSummary(EditTextPreference preference) {
        preference.getOnPreferenceChangeListener().onPreferenceChange(preference, preference.getText());
    }

    public Proxy(PreferenceActivity activity) {
        super(activity);
    }

    private static class SummaryOnChangeListener implements Preference.OnPreferenceChangeListener {

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            preference.setSummary(newValue.toString());
            return true;
        }
    }
}
