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

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;

import java.lang.reflect.Method;

public class ThemeManager extends ThemeManagerAbstract {

    protected int getThemeLight() {
        return R.style.YalpStoreThemeLight;
    }

    protected int getThemeDark() {
        return R.style.YalpStoreThemeDark;
    }

    @Override
    protected int getThemeDefault(Context context) {
        int themeId = super.getThemeDefault(context);
        if (0 == themeId && isMiui()) {
            return getThemeDark();
        } else {
            return themeId;
        }
    }

    @Override
    protected int getThemeBlack() {
        return R.style.YalpStoreThemeBlack;
    }

    @Override
    protected int getDialogThemeLight() {
        return R.style.YalpStoreDialogStyleLight;
    }

    @Override
    protected int getDialogThemeDark() {
        return R.style.YalpStoreDialogStyleDark;
    }

    private boolean isMiui() {
        return !TextUtils.isEmpty(getSystemProperty("ro.miui.ui.version.code"));
    }

    /**
     * Used for pre-lollipop devices
     */
    @SuppressLint("PrivateApi")
    private String getSystemProperty(String propertyName) {
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);
            return (String) get.invoke(c, propertyName);
        } catch (Throwable e) {
            return "";
        }
    }
}
