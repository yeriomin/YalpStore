package com.github.yeriomin.yalpstore;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
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
    public static final String PREFERENCE_BACKGROUND_UPDATE_CHECK = "PREFERENCE_BACKGROUND_UPDATE_CHECK";

    public static final String LIST_WHITE = "white";
    public static final String LIST_BLACK = "black";

    public static final String THEME_NONE = "none";
    public static final String THEME_LIGHT = "light";
    public static final String THEME_DARK = "dark";
    public static final String THEME_BLACK = "black";

    public static final int UPDATE_INTERVAL = 1000 * 60 * 60;

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
        prepareCheckUpdates((CheckBoxPreference) findPreference(PREFERENCE_BACKGROUND_UPDATE_CHECK));
    }

    private void prepareBlacklist(ListPreference blackOrWhite, final MultiSelectListPreference appList) {
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

        appList.setTitle(blackOrWhite.getValue().equals(LIST_BLACK)
            ? getString(R.string.pref_update_list_black)
            : getString(R.string.pref_update_list_white)
        );
        blackOrWhite.setSummary(blackOrWhite.getValue().equals(LIST_BLACK)
            ? getString(R.string.pref_update_list_white_or_black_black)
            : getString(R.string.pref_update_list_white_or_black_white)
        );
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
        blackOrWhite.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String value = (String) newValue;
                switch (value) {
                    case LIST_BLACK:
                        appList.setTitle(getString(R.string.pref_update_list_black));
                        preference.setSummary(getString(R.string.pref_update_list_white_or_black_black));
                        break;
                    case LIST_WHITE:
                        appList.setTitle(getString(R.string.pref_update_list_white));
                        preference.setSummary(getString(R.string.pref_update_list_white_or_black_white));
                        break;
                }
                return true;
            }
        });
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

    private void prepareCheckUpdates(CheckBoxPreference checkForUpdates) {
        checkForUpdates.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                boolean value = (Boolean) newValue;
                Intent intent = new Intent(getApplicationContext(), UpdateChecker.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                alarmManager.cancel(pendingIntent);
                if (value) {
                    alarmManager.setRepeating(
                        AlarmManager.RTC_WAKEUP,
                        System.currentTimeMillis(),
                        UPDATE_INTERVAL,
                        pendingIntent
                    );
                }
                return true;
            }
        });
    }
}