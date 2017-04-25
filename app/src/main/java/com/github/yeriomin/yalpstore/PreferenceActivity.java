package com.github.yeriomin.yalpstore;

import android.content.Context;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
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
    public static final String PREFERENCE_BACKGROUND_UPDATE_DOWNLOAD = "PREFERENCE_BACKGROUND_UPDATE_DOWNLOAD";
    public static final String PREFERENCE_BACKGROUND_UPDATE_INSTALL = "PREFERENCE_BACKGROUND_UPDATE_INSTALL";
    public static final String PREFERENCE_REQUESTED_LANGUAGE = "PREFERENCE_REQUESTED_LANGUAGE";
    public static final String PREFERENCE_DEVICE_TO_PRETEND_TO_BE = "PREFERENCE_DEVICE_TO_PRETEND_TO_BE";
    public static final String PREFERENCE_INSTALLATION_METHOD = "PREFERENCE_INSTALLATION_METHOD";
    public static final String PREFERENCE_UPDATES_ONLY = "PREFERENCE_UPDATES_ONLY";
    public static final String PREFERENCE_SHOW_SYSTEM_APPS = "PREFERENCE_SHOW_SYSTEM_APPS";

    public static final String INSTALLATION_METHOD_DEFAULT = "default";
    public static final String INSTALLATION_METHOD_ROOT = "root";
    public static final String INSTALLATION_METHOD_PRIVILEGED = "privileged";

    public static final String LIST_WHITE = "white";
    public static final String LIST_BLACK = "black";

    public static final String THEME_NONE = "none";
    public static final String THEME_LIGHT = "light";
    public static final String THEME_DARK = "dark";
    public static final String THEME_BLACK = "black";

    static public boolean getBoolean(Context context, String key) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(key, false);
    }

    static public String getString(Context context, String key) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(key, "");
    }

    static public boolean canInstallInBackground(Context context) {
        return getString(context, PREFERENCE_INSTALLATION_METHOD).equals(INSTALLATION_METHOD_ROOT)
            || getString(context, PREFERENCE_INSTALLATION_METHOD).equals(INSTALLATION_METHOD_PRIVILEGED)
        ;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        ThemeManager.setTheme(this);
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        drawBlackList();
        drawLanguages();
        drawTheme();
        drawUpdatesCheck();
        drawDevices();
        drawInstallationMethod();
    }

    private void drawBlackList() {
        PreferenceBlacklistFragment blacklistFragment = new PreferenceBlacklistFragment(this);
        blacklistFragment.setBlackOrWhite((ListPreference) findPreference(PREFERENCE_UPDATE_LIST_WHITE_OR_BLACK));
        blacklistFragment.setAppList((MultiSelectListPreference) findPreference(PREFERENCE_UPDATE_LIST));
        blacklistFragment.draw();
    }

    private void drawTheme() {
        PreferenceThemeFragment themeFragment = new PreferenceThemeFragment(this);
        themeFragment.setThemePreference((ListPreference) findPreference(PREFERENCE_UI_THEME));
        themeFragment.draw();
    }

    private void drawUpdatesCheck() {
        PreferenceCheckUpdatesFragment checkUpdatesFragment = new PreferenceCheckUpdatesFragment(this);
        checkUpdatesFragment.setCheckForUpdates((ListPreference) findPreference(PREFERENCE_BACKGROUND_UPDATE_INTERVAL));
        checkUpdatesFragment.setAlsoInstall((CheckBoxPreference) findPreference(PREFERENCE_BACKGROUND_UPDATE_INSTALL));
        checkUpdatesFragment.setAlsoDownload((CheckBoxPreference) findPreference(PREFERENCE_BACKGROUND_UPDATE_DOWNLOAD));
        checkUpdatesFragment.draw();
    }

    private void drawLanguages() {
        PreferenceLanguageFragment languageFragment = new PreferenceLanguageFragment(this);
        languageFragment.setListPreference((ListPreference) findPreference(PREFERENCE_REQUESTED_LANGUAGE));
        languageFragment.draw();
    }

    private void drawDevices() {
        PreferenceDeviceFragment languageFragment = new PreferenceDeviceFragment(this);
        languageFragment.setListPreference((ListPreference) findPreference(PREFERENCE_DEVICE_TO_PRETEND_TO_BE));
        languageFragment.draw();
    }

    private void drawInstallationMethod() {
        PreferenceInstallationMethodFragment installationMethodFragment = new PreferenceInstallationMethodFragment(this);
        installationMethodFragment.setInstallationMethodPreference((ListPreference) findPreference(PREFERENCE_INSTALLATION_METHOD));
        installationMethodFragment.draw();
    }

    private void drawUpdatePagePreferences() {
        findPreference(PREFERENCE_SHOW_SYSTEM_APPS).setOnPreferenceChangeListener(new OnUpdatePagePreferenceChangeListener());
        findPreference(PREFERENCE_UPDATES_ONLY).setOnPreferenceChangeListener(new OnUpdatePagePreferenceChangeListener());
    }

    private class OnUpdatePagePreferenceChangeListener implements Preference.OnPreferenceChangeListener {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            UpdatableAppsActivity.setNeedsUpdate(true);
            return true;
        }
    }
}