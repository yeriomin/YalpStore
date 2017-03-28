package com.github.yeriomin.yalpstore;

import android.content.Context;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.PreferenceManager;

public class PreferenceActivity extends android.preference.PreferenceActivity {

    public static final String PREFERENCE_EMAIL = "PREFERENCE_EMAIL";
    public static final String PREFERENCE_AUTH_TOKEN = "PREFERENCE_AUTH_TOKEN";
    public static final String PREFERENCE_GSF_ID = "PREFERENCE_GSF_ID";
    public static final String PREFERENCE_AUTO_INSTALL = "PREFERENCE_AUTO_INSTALL";
    public static final String PREFERENCE_HIDE_NONFREE_APPS = "PREFERENCE_HIDE_NONFREE_APPS";
    public static final String PREFERENCE_HIDE_APPS_WITH_ADS = "PREFERENCE_HIDE_APPS_WITH_ADS";
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

    static public boolean getBoolean(Context context, String key) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(key, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        ThemeManager.setTheme(this);
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        PreferenceBlacklistFragment blacklistFragment = new PreferenceBlacklistFragment(this);
        blacklistFragment.setBlackOrWhite((ListPreference) findPreference(PREFERENCE_UPDATE_LIST_WHITE_OR_BLACK));
        blacklistFragment.setAppList((MultiSelectListPreference) findPreference(PREFERENCE_UPDATE_LIST));
        blacklistFragment.draw();

        PreferenceThemeFragment themeFragment = new PreferenceThemeFragment(this);
        themeFragment.setThemePreference((ListPreference) findPreference(PREFERENCE_UI_THEME));
        themeFragment.draw();

        PreferenceCheckUpdatesFragment checkUpdatesFragment = new PreferenceCheckUpdatesFragment(this);
        checkUpdatesFragment.setCheckForUpdates((ListPreference) findPreference(PREFERENCE_BACKGROUND_UPDATE_INTERVAL));
        checkUpdatesFragment.setAlsoInstall((CheckBoxPreference) findPreference(PREFERENCE_BACKGROUND_UPDATE_INSTALL));
        checkUpdatesFragment.draw();

        PreferenceLanguageFragment languageFragment = new PreferenceLanguageFragment(this);
        languageFragment.setLanguagesPreference((ListPreference) findPreference(PREFERENCE_REQUESTED_LANGUAGE));
        languageFragment.draw();
    }
}