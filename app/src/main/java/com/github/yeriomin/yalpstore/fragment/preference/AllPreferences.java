package com.github.yeriomin.yalpstore.fragment.preference;

import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;

import com.github.yeriomin.yalpstore.PreferenceActivity;
import com.github.yeriomin.yalpstore.PreferenceUtil;

public class AllPreferences extends Abstract {

    @Override
    public void draw() {
        drawBlackList();
        drawLanguages();
        drawTheme();
        drawUpdatesCheck();
        drawDevices();
        drawInstallationMethod();
        new DownloadDirectory(activity).setPreference((EditTextPreference) activity.findPreference(PreferenceUtil.PREFERENCE_DOWNLOAD_DIRECTORY)).draw();
        new InternalStorage(activity).setPreference((CheckBoxPreference) activity.findPreference(PreferenceUtil.PREFERENCE_DOWNLOAD_INTERNAL_STORAGE)).draw();
    }

    public AllPreferences(PreferenceActivity activity) {
        super(activity);
    }


    private void drawBlackList() {
        Blacklist blacklistFragment = new Blacklist(activity);
        blacklistFragment.setBlackOrWhite((ListPreference) activity.findPreference(PreferenceUtil.PREFERENCE_UPDATE_LIST_WHITE_OR_BLACK));
        blacklistFragment.setAppList((MultiSelectListPreference) activity.findPreference(PreferenceUtil.PREFERENCE_UPDATE_LIST));
        blacklistFragment.setAutoWhitelist((CheckBoxPreference) activity.findPreference(PreferenceUtil.PREFERENCE_AUTO_WHITELIST));
        blacklistFragment.draw();
    }

    private void drawTheme() {
        Theme themeFragment = new Theme(activity);
        themeFragment.setThemePreference((ListPreference) activity.findPreference(PreferenceUtil.PREFERENCE_UI_THEME));
        themeFragment.draw();
    }

    private void drawUpdatesCheck() {
        CheckUpdates checkUpdatesFragment = new CheckUpdates(activity);
        checkUpdatesFragment.setCheckForUpdates((ListPreference) activity.findPreference(PreferenceUtil.PREFERENCE_BACKGROUND_UPDATE_INTERVAL));
        checkUpdatesFragment.setAlsoInstall((CheckBoxPreference) activity.findPreference(PreferenceUtil.PREFERENCE_BACKGROUND_UPDATE_INSTALL));
        checkUpdatesFragment.setAlsoDownload((CheckBoxPreference) activity.findPreference(PreferenceUtil.PREFERENCE_BACKGROUND_UPDATE_DOWNLOAD));
        checkUpdatesFragment.draw();
    }

    private void drawLanguages() {
        Language languageFragment = new Language(activity);
        languageFragment.setListPreference((ListPreference) activity.findPreference(PreferenceUtil.PREFERENCE_REQUESTED_LANGUAGE));
        languageFragment.draw();
    }

    private void drawDevices() {
        Device languageFragment = new Device(activity);
        languageFragment.setListPreference((ListPreference) activity.findPreference(PreferenceUtil.PREFERENCE_DEVICE_TO_PRETEND_TO_BE));
        languageFragment.draw();
    }

    private void drawInstallationMethod() {
        InstallationMethod installationMethodFragment = new InstallationMethod(activity);
        installationMethodFragment.setInstallationMethodPreference((ListPreference) activity.findPreference(PreferenceUtil.PREFERENCE_INSTALLATION_METHOD));
        installationMethodFragment.draw();
    }
}
