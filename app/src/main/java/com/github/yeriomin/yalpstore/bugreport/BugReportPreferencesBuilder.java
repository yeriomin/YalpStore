package com.github.yeriomin.yalpstore.bugreport;

import android.content.Context;
import android.preference.PreferenceManager;

import com.github.yeriomin.yalpstore.PreferenceActivity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class BugReportPreferencesBuilder extends BugReportPropertiesBuilder {

    static private final String[] PREFERENCES = {
        PreferenceActivity.PREFERENCE_APP_PROVIDED_EMAIL,
        PreferenceActivity.PREFERENCE_AUTO_INSTALL,
        PreferenceActivity.PREFERENCE_HIDE_NONFREE_APPS,
        PreferenceActivity.PREFERENCE_HIDE_APPS_WITH_ADS,
        PreferenceActivity.PREFERENCE_UPDATE_LIST_WHITE_OR_BLACK,
        PreferenceActivity.PREFERENCE_UI_THEME,
        PreferenceActivity.PREFERENCE_BACKGROUND_UPDATE_INTERVAL,
        PreferenceActivity.PREFERENCE_DELETE_APK_AFTER_INSTALL,
        PreferenceActivity.PREFERENCE_BACKGROUND_UPDATE_DOWNLOAD,
        PreferenceActivity.PREFERENCE_BACKGROUND_UPDATE_WIFI_ONLY,
        PreferenceActivity.PREFERENCE_BACKGROUND_UPDATE_INSTALL,
        PreferenceActivity.PREFERENCE_REQUESTED_LANGUAGE,
        PreferenceActivity.PREFERENCE_DEVICE_TO_PRETEND_TO_BE,
        PreferenceActivity.PREFERENCE_INSTALLATION_METHOD,
        PreferenceActivity.PREFERENCE_UPDATES_ONLY,
        PreferenceActivity.PREFERENCE_SHOW_SYSTEM_APPS,
        PreferenceActivity.PREFERENCE_NO_IMAGES,
    };

    public BugReportPreferencesBuilder(Context context) {
        super(context);
        setFileName("preferences.txt");
    }

    @Override
    public BugReportBuilder build() {
        Map<String, ?> prefs = PreferenceManager.getDefaultSharedPreferences(context).getAll();
        Set<String> whitelist = new HashSet<>(Arrays.asList(PREFERENCES));
        Map<String, String> filtered = new HashMap<>();
        for (String key: prefs.keySet()) {
            if (!whitelist.contains(key)) {
                continue;
            }
            filtered.put(key, String.valueOf(prefs.get(key)));
        }
        setContent(buildProperties(filtered));
        super.build();
        return this;
    }
}
