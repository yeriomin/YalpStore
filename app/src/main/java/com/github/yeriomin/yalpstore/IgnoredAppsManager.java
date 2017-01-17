package com.github.yeriomin.yalpstore;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class IgnoredAppsManager {

    static private final String DELIMITER = ",";

    private SharedPreferences preferences;
    private Set<String> appsToIgnore;

    public IgnoredAppsManager(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        appsToIgnore = new HashSet<>(Arrays.asList(TextUtils.split(
            preferences.getString(PreferenceActivity.PREFERENCE_IGNORED_APPS, ""),
            DELIMITER
        )));
    }

    public boolean add(String s) {
        boolean result = appsToIgnore.add(s);
        save();
        return result;
    }

    public void clear() {
        appsToIgnore.clear();
        save();
    }

    public boolean contains(String s) {
        return appsToIgnore.contains(s);
    }

    public boolean remove(String s) {
        boolean result = appsToIgnore.remove(s);
        save();
        return result;
    }

    private void save() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(
            PreferenceActivity.PREFERENCE_IGNORED_APPS,
            TextUtils.join(DELIMITER, appsToIgnore)
        );
        editor.apply();
    }
}
