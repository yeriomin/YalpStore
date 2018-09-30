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

import android.content.Context;
import android.content.SharedPreferences;

public class FirstLaunchChecker {

    static private final String FIRST_LOGIN = "FIRST_LOGIN";

    private SharedPreferences prefs;

    public FirstLaunchChecker(Context context) {
        prefs = PreferenceUtil.getDefaultSharedPreferences(context);
    }

    public boolean isFirstLogin() {
        return prefs.getBoolean(FIRST_LOGIN, true);
    }

    public void setLoggedIn() {
        prefs.edit()
            .putBoolean(FIRST_LOGIN, false)
            .apply()
        ;
    }
}
