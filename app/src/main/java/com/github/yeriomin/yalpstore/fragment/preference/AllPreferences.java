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

import android.annotation.TargetApi;
import android.os.Build;
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
        drawTheme();
        drawUpdatesCheck();
        drawInstallationMethod();
        new DownloadDirectory(activity).setPreference((EditTextPreference) activity.findPreference(PreferenceUtil.PREFERENCE_DOWNLOAD_DIRECTORY)).draw();
        new InternalStorage(activity).setPreference((CheckBoxPreference) activity.findPreference(PreferenceUtil.PREFERENCE_DOWNLOAD_INTERNAL_STORAGE)).draw();
        new Proxy(activity).draw();
    }

    public AllPreferences(PreferenceActivity activity) {
        super(activity);
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
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

    private void drawInstallationMethod() {
        InstallationMethod installationMethodFragment = new InstallationMethod(activity);
        installationMethodFragment.setInstallationMethodPreference((ListPreference) activity.findPreference(PreferenceUtil.PREFERENCE_INSTALLATION_METHOD));
        installationMethodFragment.draw();
    }
}
