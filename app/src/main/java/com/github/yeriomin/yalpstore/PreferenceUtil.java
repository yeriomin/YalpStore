package com.github.yeriomin.yalpstore;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class PreferenceUtil {

    public static final String PREFERENCE_AUTO_INSTALL = "PREFERENCE_AUTO_INSTALL";
    public static final String PREFERENCE_UPDATE_LIST_WHITE_OR_BLACK = "PREFERENCE_UPDATE_LIST_WHITE_OR_BLACK";
    public static final String PREFERENCE_UPDATE_LIST = "PREFERENCE_UPDATE_LIST";
    public static final String PREFERENCE_UI_THEME = "PREFERENCE_UI_THEME";
    public static final String PREFERENCE_BACKGROUND_UPDATE_INTERVAL = "PREFERENCE_BACKGROUND_UPDATE_INTERVAL";
    public static final String PREFERENCE_DELETE_APK_AFTER_INSTALL = "PREFERENCE_DELETE_APK_AFTER_INSTALL";
    public static final String PREFERENCE_BACKGROUND_UPDATE_DOWNLOAD = "PREFERENCE_BACKGROUND_UPDATE_DOWNLOAD";
    public static final String PREFERENCE_BACKGROUND_UPDATE_WIFI_ONLY = "PREFERENCE_BACKGROUND_UPDATE_WIFI_ONLY";
    public static final String PREFERENCE_BACKGROUND_UPDATE_INSTALL = "PREFERENCE_BACKGROUND_UPDATE_INSTALL";
    public static final String PREFERENCE_REQUESTED_LANGUAGE = "PREFERENCE_REQUESTED_LANGUAGE";
    public static final String PREFERENCE_DEVICE_TO_PRETEND_TO_BE = "PREFERENCE_DEVICE_TO_PRETEND_TO_BE";
    public static final String PREFERENCE_INSTALLATION_METHOD = "PREFERENCE_INSTALLATION_METHOD";
    public static final String PREFERENCE_NO_IMAGES = "PREFERENCE_NO_IMAGES";
    public static final String PREFERENCE_DOWNLOAD_DIRECTORY = "PREFERENCE_DOWNLOAD_DIRECTORY";
    public static final String PREFERENCE_DOWNLOAD_DELTAS = "PREFERENCE_DOWNLOAD_DELTAS";
    public static final String PREFERENCE_AUTO_WHITELIST = "PREFERENCE_AUTO_WHITELIST";
    public static final String PREFERENCE_DOWNLOAD_INTERNAL_STORAGE = "PREFERENCE_DOWNLOAD_INTERNAL_STORAGE";

    public static final String INSTALLATION_METHOD_DEFAULT = "default";
    public static final String INSTALLATION_METHOD_ROOT = "root";
    public static final String INSTALLATION_METHOD_PRIVILEGED = "privileged";

    public static final String LIST_WHITE = "white";
    public static final String LIST_BLACK = "black";

    public static final String THEME_NONE = "none";
    public static final String THEME_LIGHT = "light";
    public static final String THEME_DARK = "dark";
    public static final String THEME_BLACK = "black";

    private static final String DELIMITER = ",";

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

    static public Set<String> getStringSet(Context context, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return preferences.getStringSet(key, new HashSet<String>());
        } else {
            return new HashSet<>(Arrays.asList(TextUtils.split(
                preferences.getString(key, ""),
                DELIMITER
            )));
        }
    }

    static public void putStringSet(Context context, String key, Set<String> set) {
        putStringSet(PreferenceManager.getDefaultSharedPreferences(context), key, set);
    }

    static public void putStringSet(SharedPreferences preferences, String key, Set<String> set) {
        SharedPreferences.Editor editor = preferences.edit();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            editor.putStringSet(key, set).apply();
        } else {
            editor.putString(key, TextUtils.join(DELIMITER, set)).commit();
        }
    }
}
