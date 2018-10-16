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

package com.github.yeriomin.yalpstore;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.util.TypedValue;

abstract public class ThemeManagerAbstract {

    abstract protected int getThemeLight();
    abstract protected int getThemeDark();
    abstract protected int getThemeBlack();
    abstract protected int getDialogThemeLight();
    abstract protected int getDialogThemeDark();

    public void setTheme(Activity activity) {
        SharedPreferences prefs = PreferenceUtil.getDefaultSharedPreferences(activity);
        String theme = prefs.getString(PreferenceUtil.PREFERENCE_UI_THEME, PreferenceUtil.THEME_NONE);
        int themeId = getThemeId(theme, activity);
        if (themeId != 0) {
            activity.setTheme(themeId);
        }
        if (theme.equals(PreferenceUtil.THEME_BLACK)) {
            activity.getWindow().setBackgroundDrawableResource(android.R.color.black);
        }
    }

    public int getDialogThemeId(Context context) {
        SharedPreferences prefs = PreferenceUtil.getDefaultSharedPreferences(context);
        String theme = prefs.getString(PreferenceUtil.PREFERENCE_UI_THEME, PreferenceUtil.THEME_NONE);
        switch (theme) {
            default:
            case PreferenceUtil.THEME_NONE:
                return 0;
            case PreferenceUtil.THEME_LIGHT:
                return getDialogThemeLight();
            case PreferenceUtil.THEME_DARK:
            case PreferenceUtil.THEME_BLACK:
                return getDialogThemeDark();
        }
    }

    private int getThemeId(String theme, Context context) {
        switch (theme) {
            default:
            case PreferenceUtil.THEME_NONE:
                return getThemeDefault(context);
            case PreferenceUtil.THEME_LIGHT:
                return getThemeLight();
            case PreferenceUtil.THEME_DARK:
                return getThemeDark();
            case PreferenceUtil.THEME_BLACK:
                return getThemeBlack();
        }
    }

    protected int getThemeDefault(Context context) {
        if (isAmazonTv(context)) {
            return getThemeDark();
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
