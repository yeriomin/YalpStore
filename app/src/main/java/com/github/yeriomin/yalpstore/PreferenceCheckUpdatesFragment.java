package com.github.yeriomin.yalpstore;

import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;

public class PreferenceCheckUpdatesFragment extends PreferenceFragment {

    private ListPreference checkForUpdates;
    private CheckBoxPreference alsoInstall;

    public PreferenceCheckUpdatesFragment(PreferenceActivity activity) {
        super(activity);
    }

    public void setCheckForUpdates(ListPreference checkForUpdates) {
        this.checkForUpdates = checkForUpdates;
    }

    public void setAlsoInstall(CheckBoxPreference alsoInstall) {
        this.alsoInstall = alsoInstall;
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
                alsoInstall.setEnabled(interval != 0);
                return true;
            }
        });
        checkForUpdates.getOnPreferenceChangeListener().onPreferenceChange(checkForUpdates, checkForUpdates.getValue());
        alsoInstall.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if ((Boolean) newValue) {
                    new CheckSuTask(activity).execute();
                }
                return true;
            }
        });
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
            default:
                summaryId = R.string.pref_background_update_interval_never;
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
}
