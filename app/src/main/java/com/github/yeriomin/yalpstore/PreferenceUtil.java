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
import android.os.Build;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import java.net.InetSocketAddress;
import java.net.Proxy;
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
    public static final String PREFERENCE_USE_TOR = "PREFERENCE_USE_TOR";
    public static final String PREFERENCE_ENABLE_PROXY = "PREFERENCE_ENABLE_PROXY";
    public static final String PREFERENCE_PROXY_TYPE = "PREFERENCE_PROXY_TYPE";
    public static final String PREFERENCE_PROXY_HOST = "PREFERENCE_PROXY_HOST";
    public static final String PREFERENCE_PROXY_PORT = "PREFERENCE_PROXY_PORT";
    public static final String PREFERENCE_EXODUS = "PREFERENCE_EXODUS";

    public static final String INSTALLATION_METHOD_DEFAULT = "default";
    public static final String INSTALLATION_METHOD_ROOT = "root";
    public static final String INSTALLATION_METHOD_PRIVILEGED = "privileged";

    public static final String LIST_WHITE = "white";
    public static final String LIST_BLACK = "black";

    public static final String THEME_NONE = "none";
    public static final String THEME_LIGHT = "light";
    public static final String THEME_DARK = "dark";
    public static final String THEME_BLACK = "black";

    public static final String PROXY_HTTP = "PROXY_HTTP";
    public static final String PROXY_SOCKS = "PROXY_SOCKS";

    private static final String DELIMITER = ",";

    public static SharedPreferences sharedPreferences;

    static public SharedPreferences getDefaultSharedPreferences(Context context) {
        if (null == sharedPreferences) {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        }
        return sharedPreferences;
    }

    static public boolean getBoolean(Context context, String key) {
        return getDefaultSharedPreferences(context).getBoolean(key, false);
    }

    static public String getString(Context context, String key) {
        return getDefaultSharedPreferences(context).getString(key, "");
    }

    static public boolean canInstallInBackground(Context context) {
        return getString(context, PREFERENCE_INSTALLATION_METHOD).equals(INSTALLATION_METHOD_ROOT)
            || getString(context, PREFERENCE_INSTALLATION_METHOD).equals(INSTALLATION_METHOD_PRIVILEGED)
        ;
    }

    static public Set<String> getStringSet(Context context, String key) {
        return getStringSet(getDefaultSharedPreferences(context), key);
    }

    static public Set<String> getStringSet(SharedPreferences preferences, String key) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            try {
                return preferences.getStringSet(key, new HashSet<String>());
            } catch (ClassCastException e) {
                return getStringSetCompat(preferences, key);
            }
        } else {
            return getStringSetCompat(preferences, key);
        }
    }

    static public Set<String> getStringSetCompat(SharedPreferences preferences, String key) {
        return new HashSet<>(Arrays.asList(TextUtils.split(
            preferences.getString(key, ""),
            DELIMITER
        )));
    }

    static public void putStringSet(Context context, String key, Set<String> set) {
        putStringSet(getDefaultSharedPreferences(context), key, set);
    }

    static public void putStringSet(SharedPreferences preferences, String key, Set<String> set) {
        SharedPreferences.Editor editor = preferences.edit();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            editor.putStringSet(key, set).apply();
        } else {
            editor.putString(key, TextUtils.join(DELIMITER, set)).commit();
        }
    }

    static public void prefillInstallationMethod(Context context) {
        SharedPreferences preferences = getDefaultSharedPreferences(context);
        if (TextUtils.isEmpty(preferences.getString(PreferenceUtil.PREFERENCE_INSTALLATION_METHOD, ""))
            && YalpStorePermissionManager.hasInstallPermission(context)
        ) {
            preferences.edit().putString(PreferenceUtil.PREFERENCE_INSTALLATION_METHOD, PreferenceUtil.INSTALLATION_METHOD_PRIVILEGED).apply();
        }
    }

    static public Proxy getProxy(Context context) {
        SharedPreferences prefs = getDefaultSharedPreferences(context);
        if (!prefs.getBoolean(PREFERENCE_ENABLE_PROXY, false)) {
            return null;
        }
        return new Proxy(
            prefs.getString(PREFERENCE_PROXY_TYPE, PROXY_HTTP).equals(PROXY_HTTP) ? Proxy.Type.HTTP : Proxy.Type.SOCKS,
            new InetSocketAddress(
                prefs.getString(PREFERENCE_PROXY_HOST, "127.0.0.1"),
                Util.parseInt(prefs.getString(PREFERENCE_PROXY_PORT, "8118"), 8118)
            )
        );
    }
}
