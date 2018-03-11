package com.github.yeriomin.yalpstore;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.TypedValue;

abstract public class ThemeManagerAbstract {

    protected Activity activity;

    abstract protected int getThemeLight();
    abstract protected int getThemeDark();

    public ThemeManagerAbstract(Activity activity) {
        this.activity = activity;
    }

    public void setTheme() {
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

    private int getThemeId(String theme, Activity activity) {
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

    private int getThemeDefault(Activity activity) {
        if (isAmazonTv(activity)) {
            getThemeDark();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return isWindowBackgroundDark(activity) ? getThemeDark() : getThemeLight();
        }
        return 0;
    }

    static private boolean isAmazonTv(Activity activity) {
        return ((YalpStoreApplication) activity.getApplication()).isTv() && Build.MANUFACTURER.toLowerCase().contains("amazon");
    }

    static boolean isWindowBackgroundDark(Activity activity) {
        TypedValue color = new TypedValue();
        try {
            activity.getTheme().resolveAttribute(android.R.attr.colorBackground, color, true);
            return Color.red(color.data) < 128 && Color.green(color.data) < 128 && Color.blue(color.data) < 128;
        } catch (RuntimeException e) {
            return false;
        }
    }
}
