package com.github.yeriomin.yalpstore;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;

public class ThemeManager {

    static public void setTheme(Activity activity) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        String theme = prefs.getString(PreferenceActivity.PREFERENCE_UI_THEME, PreferenceActivity.THEME_NONE);
        int themeId = getThemeId(theme, activity);
        if (themeId != 0) {
            activity.setTheme(themeId);
        }
        if (theme.equals(PreferenceActivity.THEME_BLACK)) {
            activity.getWindow().setBackgroundDrawableResource(android.R.color.black);
        }
    }

    static private int getThemeId(String theme, Activity activity) {
        switch (theme) {
            default:
            case PreferenceActivity.THEME_NONE:
                return getThemeDefault(activity);
            case PreferenceActivity.THEME_LIGHT:
                return getThemeLight();
            case PreferenceActivity.THEME_DARK:
            case PreferenceActivity.THEME_BLACK:
                return getThemeDark();
        }
    }

    static private int getThemeDefault(Activity activity) {
        if (isAmazonTv(activity)) {
            getThemeDark();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return R.style.Theme_DeviceDefault_Almost;
        }
        return 0;
    }

    static private int getThemeLight() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return android.R.style.Theme_Material_Light;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return android.R.style.Theme_Holo_Light;
        } else {
            return android.R.style.Theme_Light;
        }
    }

    static private int getThemeDark() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return android.R.style.Theme_Material;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return android.R.style.Theme_Holo;
        } else {
            return android.R.style.Theme;
        }
    }

    static private boolean isAmazonTv(Activity activity) {
        return ((YalpStoreApplication) activity.getApplication()).isTv() && Build.MANUFACTURER.toLowerCase().contains("amazon");
    }
}
