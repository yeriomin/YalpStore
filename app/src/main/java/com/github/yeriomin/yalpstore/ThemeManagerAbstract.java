package com.github.yeriomin.yalpstore;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.TypedValue;

abstract public class ThemeManagerAbstract {

    abstract protected int getThemeLight();
    abstract protected int getThemeDark();

    public void setTheme(Activity activity) {
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

    public int getThemeId(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String theme = prefs.getString(PreferenceActivity.PREFERENCE_UI_THEME, PreferenceActivity.THEME_NONE);
        return getThemeId(theme, context);
    }

    private int getThemeId(String theme, Context context) {
        switch (theme) {
            default:
            case PreferenceActivity.THEME_NONE:
                return getThemeDefault(context);
            case PreferenceActivity.THEME_LIGHT:
                return getThemeLight();
            case PreferenceActivity.THEME_DARK:
            case PreferenceActivity.THEME_BLACK:
                return getThemeDark();
        }
    }

    private int getThemeDefault(Context context) {
        if (isAmazonTv(context)) {
            getThemeDark();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return isWindowBackgroundDark(context) ? getThemeDark() : getThemeLight();
        }
        return 0;
    }

    static private boolean isAmazonTv(Context context) {
        return ((YalpStoreApplication) context.getApplicationContext()).isTv() && Build.MANUFACTURER.toLowerCase().contains("amazon");
    }

    static boolean isWindowBackgroundDark(Context context) {
        TypedValue color = new TypedValue();
        try {
            context.getTheme().resolveAttribute(android.R.attr.colorBackground, color, true);
            return Color.red(color.data) < 128 && Color.green(color.data) < 128 && Color.blue(color.data) < 128;
        } catch (RuntimeException e) {
            return false;
        }
    }
}
