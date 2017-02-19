package com.github.yeriomin.yalpstore;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PreferenceActivity extends android.preference.PreferenceActivity {

    public static final String PREFERENCE_EMAIL = "PREFERENCE_EMAIL";
    public static final String PREFERENCE_AUTH_TOKEN = "PREFERENCE_AUTH_TOKEN";
    public static final String PREFERENCE_GSF_ID = "PREFERENCE_GSF_ID";
    public static final String PREFERENCE_AUTO_INSTALL = "PREFERENCE_AUTO_INSTALL";
    public static final String PREFERENCE_HIDE_NONFREE_APPS = "PREFERENCE_HIDE_NONFREE_APPS";
    public static final String PREFERENCE_UPDATE_LIST_WHITE_OR_BLACK = "PREFERENCE_UPDATE_LIST_WHITE_OR_BLACK";
    public static final String PREFERENCE_UPDATE_LIST = "PREFERENCE_UPDATE_LIST";
    public static final String PREFERENCE_UI_THEME = "PREFERENCE_UI_THEME";
    public static final String PREFERENCE_BACKGROUND_UPDATE_INTERVAL = "PREFERENCE_BACKGROUND_UPDATE_INTERVAL";
    public static final String PREFERENCE_DELETE_APK_AFTER_INSTALL = "PREFERENCE_DELETE_APK_AFTER_INSTALL";

    public static final String LIST_WHITE = "white";
    public static final String LIST_BLACK = "black";

    public static final String THEME_NONE = "none";
    public static final String THEME_LIGHT = "light";
    public static final String THEME_DARK = "dark";
    public static final String THEME_BLACK = "black";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        ThemeManager.setTheme(this);
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        prepareBlacklist(
            (ListPreference) findPreference(PREFERENCE_UPDATE_LIST_WHITE_OR_BLACK),
            (MultiSelectListPreference) findPreference(PREFERENCE_UPDATE_LIST)
        );
        prepareTheme((ListPreference) findPreference(PREFERENCE_UI_THEME));
        prepareCheckUpdates((ListPreference) findPreference(PREFERENCE_BACKGROUND_UPDATE_INTERVAL));
    }

    private void prepareBlacklist(ListPreference blackOrWhite, final MultiSelectListPreference appList) {
        Map<String, String> appNames = getInstalledAppNames();
        List<String> labels = new ArrayList<>(appNames.keySet());
        Collections.sort(
            labels,
            new Comparator<Object>() {
                public int compare(Object o1, Object o2) {
                    String s1 = (String) o1;
                    String s2 = (String) o2;
                    return s1.toLowerCase().compareTo(s2.toLowerCase());
                }
            }
        );
        List<String> values = new ArrayList<>();
        for (String label: labels) {
            values.add(appNames.get(label));
        }
        int appCount = values.size();
        appList.setEntries(labels.toArray(new String[appCount]));
        appList.setEntryValues(values.toArray(new String[appCount]));
        appList.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                UpdatableAppsActivity.setNeedsUpdate(true);
                return true;
            }
        });

        Preference.OnPreferenceChangeListener listener = new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String value = (String) newValue;
                boolean isBlackList = value.equals(LIST_BLACK);
                appList.setTitle(getString(isBlackList ? R.string.pref_update_list_black : R.string.pref_update_list_white));
                preference.setSummary(getString(isBlackList ? R.string.pref_update_list_white_or_black_black : R.string.pref_update_list_white_or_black_white));
                return true;
            }
        };
        blackOrWhite.setOnPreferenceChangeListener(listener);
        listener.onPreferenceChange(blackOrWhite, blackOrWhite.getValue());
    }

    private Map<String, String> getInstalledAppNames() {
        PackageManager pm = getPackageManager();
        List<PackageInfo> packages = pm.getInstalledPackages(PackageManager.GET_META_DATA);
        Map<String, String> appNames = new HashMap<>();
        for (PackageInfo info: packages) {
            if ((info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                // This is a system app - skipping
                continue;
            }
            appNames.put(pm.getApplicationLabel(info.applicationInfo).toString(), info.packageName);
        }
        return appNames;
    }

    private void prepareTheme(ListPreference theme) {
        theme.setSummary(getString(getThemeSummaryStringId(theme.getValue())));
        theme.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, final Object newValue) {
                preference.setSummary(getString(getThemeSummaryStringId((String) newValue)));
                return true;
            }
        });
    }

    private int getThemeSummaryStringId(String theme) {
        int summaryId;
        switch (theme) {
            case THEME_LIGHT:
                summaryId = R.string.pref_ui_theme_light;
                break;
            case THEME_DARK:
                summaryId = R.string.pref_ui_theme_dark;
                break;
            case THEME_BLACK:
                summaryId = R.string.pref_ui_theme_black;
                break;
            case THEME_NONE:
            default:
                summaryId = R.string.pref_ui_theme_none;
                break;
        }
        return summaryId;
    }

    private void prepareCheckUpdates(ListPreference checkForUpdates) {
        checkForUpdates.setSummary(getString(getUpdateSummaryStringId(checkForUpdates.getValue())));
        checkForUpdates.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                int interval = Integer.parseInt((String) newValue);
                Intent intent = new Intent(getApplicationContext(), UpdateChecker.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                alarmManager.cancel(pendingIntent);
                if (interval > 0) {
                    alarmManager.setRepeating(
                        AlarmManager.RTC_WAKEUP,
                        System.currentTimeMillis(),
                        interval,
                        pendingIntent
                    );
                }
                preference.setSummary(getString(getUpdateSummaryStringId((String) newValue)));
                return true;
            }
        });
    }

    private int getUpdateSummaryStringId(String intervalString) {
        int summaryId;
        final int hour = 1000 * 60 * 60;
        final int day = hour * 24;
        final int week = day * 7;
        int interval;
        try {
            interval = Integer.parseInt(intervalString);
        } catch (NumberFormatException e) {
            interval = 0;
        }
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
}