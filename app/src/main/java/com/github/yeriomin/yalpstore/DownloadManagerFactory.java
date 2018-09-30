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
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

public class DownloadManagerFactory {

    private static final String DOWNLOAD_MANAGER_PACKAGE_NAME = "com.android.providers.downloads";

    static public DownloadManagerInterface get(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB
            || !nativeDownloadManagerEnabled(context)
            || nougatVpn(context)
            || PreferenceUtil.getBoolean(context, PreferenceUtil.PREFERENCE_DOWNLOAD_INTERNAL_STORAGE)
            || PreferenceUtil.getBoolean(context, PreferenceUtil.PREFERENCE_USE_TOR)
            || PreferenceUtil.getBoolean(context, PreferenceUtil.PREFERENCE_ENABLE_PROXY)
        ) {
            return new DownloadManagerFake(context);
        } else {
            return new DownloadManagerAdapter(context);
        }
    }

    static private boolean nativeDownloadManagerEnabled(Context context) {
        int state = PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
        try {
            state = context.getPackageManager().getApplicationEnabledSetting(DOWNLOAD_MANAGER_PACKAGE_NAME);
        } catch (Throwable e) {
            Log.w(DownloadManagerFactory.class.getSimpleName(), "Could not check DownloadManager status: " + e.getMessage());
        }
        return !(state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED
            || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER
            || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED
        );
    }

    static private boolean nougatVpn(Context context) {
        return (Build.VERSION.SDK_INT == Build.VERSION_CODES.N || Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1) && NetworkUtil.isVpn(context);
    }
}
