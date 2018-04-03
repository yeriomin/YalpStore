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

import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;

import com.github.yeriomin.yalpstore.Paths;
import com.github.yeriomin.yalpstore.PreferenceActivity;
import com.github.yeriomin.yalpstore.PreferenceUtil;

import java.io.File;

public class InternalStorage extends Abstract {

    private CheckBoxPreference preference;

    public InternalStorage setPreference(CheckBoxPreference preference) {
        this.preference = preference;
        return this;
    }

    @Override
    public void draw() {
        preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if ((Boolean) newValue) {
                    activity.findPreference(PreferenceUtil.PREFERENCE_DOWNLOAD_DIRECTORY).setSummary(activity.getFilesDir().getAbsolutePath());
                    ((CheckBoxPreference) activity.findPreference(PreferenceUtil.PREFERENCE_AUTO_INSTALL)).setChecked(true);
                    ((CheckBoxPreference) activity.findPreference(PreferenceUtil.PREFERENCE_DELETE_APK_AFTER_INSTALL)).setChecked(true);
                } else {
                    activity.findPreference(PreferenceUtil.PREFERENCE_DOWNLOAD_DIRECTORY).setSummary(new File(
                        Paths.getStorageRoot(activity),
                        PreferenceManager.getDefaultSharedPreferences(activity).getString(PreferenceUtil.PREFERENCE_DOWNLOAD_DIRECTORY, "")
                    ).getAbsolutePath());
                }
                return true;
            }
        });
    }

    public InternalStorage(PreferenceActivity activity) {
        super(activity);
    }
}
