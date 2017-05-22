package com.github.yeriomin.yalpstore.fragment.preference;

import android.Manifest;
import android.content.pm.PackageManager;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;

import com.github.yeriomin.yalpstore.BuildConfig;
import com.github.yeriomin.yalpstore.CheckSuTask;
import com.github.yeriomin.yalpstore.PreferenceActivity;
import com.github.yeriomin.yalpstore.R;
import com.github.yeriomin.yalpstore.UpdateChecker;

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
                int interval = parseInt((String) newValue);
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
        int interval = parseInt(intervalString);
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

    private int parseInt(String string) {
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException e) {
            return 0;
        }
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
