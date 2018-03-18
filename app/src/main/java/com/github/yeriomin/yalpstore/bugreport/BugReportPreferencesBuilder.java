package com.github.yeriomin.yalpstore.bugreport;

import android.content.Context;
import android.preference.PreferenceManager;

import com.github.yeriomin.yalpstore.PlayStoreApiAuthenticator;
import com.github.yeriomin.yalpstore.PreferenceUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class BugReportPreferencesBuilder extends BugReportPropertiesBuilder {

    static private final String[] PREFERENCES = {
        PlayStoreApiAuthenticator.PREFERENCE_APP_PROVIDED_EMAIL,
        PreferenceUtil.PREFERENCE_AUTO_INSTALL,
        PreferenceUtil.PREFERENCE_UPDATE_LIST_WHITE_OR_BLACK,
        PreferenceUtil.PREFERENCE_UI_THEME,
        PreferenceUtil.PREFERENCE_BACKGROUND_UPDATE_INTERVAL,
        PreferenceUtil.PREFERENCE_DELETE_APK_AFTER_INSTALL,
        PreferenceUtil.PREFERENCE_BACKGROUND_UPDATE_DOWNLOAD,
        PreferenceUtil.PREFERENCE_BACKGROUND_UPDATE_WIFI_ONLY,
        PreferenceUtil.PREFERENCE_BACKGROUND_UPDATE_INSTALL,
        PreferenceUtil.PREFERENCE_REQUESTED_LANGUAGE,
        PreferenceUtil.PREFERENCE_DEVICE_TO_PRETEND_TO_BE,
        PreferenceUtil.PREFERENCE_INSTALLATION_METHOD,
        PreferenceUtil.PREFERENCE_NO_IMAGES,
        PreferenceUtil.PREFERENCE_DOWNLOAD_INTERNAL_STORAGE,
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
