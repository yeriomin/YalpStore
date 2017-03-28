package com.github.yeriomin.yalpstore;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.text.TextUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
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
    public static final String PREFERENCE_BACKGROUND_UPDATE_INSTALL = "PREFERENCE_BACKGROUND_UPDATE_INSTALL";
    public static final String PREFERENCE_REQUESTED_LANGUAGE = "PREFERENCE_REQUESTED_LANGUAGE";

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
        prepareCheckUpdates(
            (ListPreference) findPreference(PREFERENCE_BACKGROUND_UPDATE_INTERVAL),
            (CheckBoxPreference) findPreference(PREFERENCE_BACKGROUND_UPDATE_INSTALL)
        );
        prepareLanguageList((ListPreference) findPreference(PREFERENCE_REQUESTED_LANGUAGE));
    }

    private void prepareBlacklist(ListPreference blackOrWhite, final MultiSelectListPreference appList) {
        Map<String, String> appNames = getInstalledAppNames();
        int count = appNames.size();
        appList.setEntries(appNames.values().toArray(new String[count]));
        appList.setEntryValues(appNames.keySet().toArray(new String[count]));
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
            appNames.put(info.packageName, pm.getApplicationLabel(info.applicationInfo).toString());
        }
        return Util.sort(appNames);
    }

    private void prepareTheme(ListPreference theme) {
        Preference.OnPreferenceChangeListener listener = new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, final Object newValue) {
                preference.setSummary(getString(getThemeSummaryStringId((String) newValue)));
                return true;
            }
        };
        listener.onPreferenceChange(theme, theme.getValue());
        theme.setOnPreferenceChangeListener(listener);
    }

    private int getThemeSummaryStringId(String theme) {
        if (null == theme) {
            return R.string.pref_ui_theme_none;
        }
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

    private void prepareCheckUpdates(ListPreference checkForUpdates, final CheckBoxPreference alsoInstall) {
        checkForUpdates.setSummary(getString(getUpdateSummaryStringId(checkForUpdates.getValue())));
        checkForUpdates.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                int interval = parseInt((String) newValue);
                UpdateChecker.enable(getApplicationContext(), interval);
                preference.setSummary(getString(getUpdateSummaryStringId((String) newValue)));
                alsoInstall.setEnabled(interval != 0);
                return true;
            }
        });
        checkForUpdates.getOnPreferenceChangeListener().onPreferenceChange(checkForUpdates, checkForUpdates.getValue());
        alsoInstall.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if ((Boolean) newValue) {
                    new CheckSuTask(PreferenceActivity.this).execute();
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

    private void prepareLanguageList(ListPreference languagesPreference) {
        final Map<String, String> localeList = getLanguages();
        int count = localeList.size();
        languagesPreference.setEntries(localeList.values().toArray(new CharSequence[count]));
        languagesPreference.setEntryValues(localeList.keySet().toArray(new CharSequence[count]));
        Preference.OnPreferenceChangeListener listener = new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (TextUtils.isEmpty((CharSequence) newValue)) {
                    preference.setSummary(getString(R.string.pref_requested_language_default));
                    return true;
                }
                preference.setSummary(localeList.get(newValue));
                try {
                    new PlayStoreApiAuthenticator(PreferenceActivity.this).getApi().setLocale(new Locale((String) newValue));
                } catch (IOException e) {
                    // Should be impossible to get to preferences with incorrect credentials
                }
                return true;
            }
        };
        languagesPreference.setOnPreferenceChangeListener(listener);
        listener.onPreferenceChange(languagesPreference, languagesPreference.getValue());
    }

    private Map<String, String> getLanguages() {
        Map<String, String> languages = new HashMap<>();
        for (Locale locale: Locale.getAvailableLocales()) {
            String displayName = locale.getDisplayName();
            displayName = displayName.substring(0, 1).toUpperCase() + displayName.substring(1);
            languages.put(locale.toString(), displayName);
        }
        languages = Util.sort(languages);
        Util.addToStart((LinkedHashMap<String, String>) languages, "", getString(R.string.pref_requested_language_default));
        return languages;
    }
}