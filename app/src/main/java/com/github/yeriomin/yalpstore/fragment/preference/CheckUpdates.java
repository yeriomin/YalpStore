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

import android.Manifest;
import android.content.pm.PackageManager;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;

import com.github.yeriomin.yalpstore.BuildConfig;
import com.github.yeriomin.yalpstore.PreferenceActivity;
import com.github.yeriomin.yalpstore.R;
import com.github.yeriomin.yalpstore.UpdateChecker;
import com.github.yeriomin.yalpstore.Util;
import com.github.yeriomin.yalpstore.task.CheckSuTask;

public class CheckUpdates extends Abstract {

    private ListPreference checkForUpdates;
    private CheckBoxPreference alsoInstall;
    private CheckBoxPreference alsoDownload;

    public CheckUpdates(PreferenceActivity activity) {
        super(activity);
    }

    public void setCheckForUpdates(ListPreference checkForUpdates) {
        this.checkForUpdates = checkForUpdates;
    }

    public void setAlsoInstall(CheckBoxPreference alsoInstall) {
        this.alsoInstall = alsoInstall;
    }

    public void setAlsoDownload(CheckBoxPreference alsoDownload) {
        this.alsoDownload = alsoDownload;
    }

    @Override
    public void draw() {
        checkForUpdates.setSummary(activity.getString(getUpdateSummaryStringId(checkForUpdates.getValue())));
        checkForUpdates.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                int interval = Util.parseInt((String) newValue, 0);
                UpdateChecker.enable(activity, interval);
                preference.setSummary(activity.getString(getUpdateSummaryStringId((String) newValue)));
                alsoDownload.setEnabled(interval > 0);
                alsoInstall.setEnabled(interval > 0);
                return true;
            }
        });
        checkForUpdates.getOnPreferenceChangeListener().onPreferenceChange(checkForUpdates, checkForUpdates.getValue());
        alsoInstall.setOnPreferenceChangeListener(new AlsoInstallOnPreferenceChangeListener());
    }

    private int getUpdateSummaryStringId(String intervalString) {
        int summaryId;
        final int hour = 1000 * 60 * 60;
        final int day = hour * 24;
        final int week = day * 7;
        int interval = Util.parseInt(intervalString, 0);
        switch (interval) {
            case hour:
                summaryId = R.string.pref_background_update_interval_hourly;
                break;
            case day:
                summaryId = R.string.pref_background_update_interval_daily;
                break;
            case week:
                summaryId = R.string.pref_background_update_interval_weekly;
                break;
            case 0:
                summaryId = R.string.pref_background_update_interval_never;
                break;
            default:
            case -1:
                summaryId = R.string.pref_background_update_interval_manually;
                break;
        }
        return summaryId;
    }

    private class AlsoInstallOnPreferenceChangeListener implements Preference.OnPreferenceChangeListener {

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            if ((Boolean) newValue) {
                if (isPrivileged()) {
                    return true;
                } else {
                    new CheckSuTask(activity).execute();
                }
            }
            return true;
        }

        private boolean isPrivileged() {
            return  activity.getPackageManager().checkPermission(Manifest.permission.INSTALL_PACKAGES, BuildConfig.APPLICATION_ID) == PackageManager.PERMISSION_GRANTED;
        }
    }
}
