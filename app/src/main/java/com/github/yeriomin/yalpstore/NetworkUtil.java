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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.os.Build;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import info.guardianproject.netcipher.NetCipher;

import static android.content.Context.CONNECTIVITY_SERVICE;
import static android.net.ConnectivityManager.TYPE_MOBILE;
import static android.net.ConnectivityManager.TYPE_MOBILE_DUN;
import static android.net.ConnectivityManager.TYPE_MOBILE_HIPRI;
import static android.net.ConnectivityManager.TYPE_MOBILE_MMS;
import static android.net.ConnectivityManager.TYPE_MOBILE_SUPL;
import static android.net.ConnectivityManager.TYPE_WIFI;
import static android.net.ConnectivityManager.TYPE_WIMAX;

public class NetworkUtil {

    private static final String CONNECTIVITY_CHECK_URL = "http://connectivitycheck.gstatic.com/generate_204";
    private static final int EXPECTED_HTTP_RESPONSE_CODE = 204;

    static public HttpURLConnection getHttpURLConnection(String url) throws IOException {
        return getHttpURLConnection(new URL(url));
    }

    static public HttpURLConnection getHttpURLConnection(URL url) throws IOException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            TrafficStats.setThreadStatsTag(Thread.currentThread().hashCode());
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
            return (HttpURLConnection) url.openConnection();
        }
        // Depending on android version, turning compatibility mode off causes connection failures.
        // Since google servers are well maintained and don't have weak ciphers on,
        // turning compatibility on for google servers only should not be a problem.
        return NetCipher.getHttpURLConnection(url, url.getHost().endsWith("google.com"));
    }

    static public boolean isNetworkAvailable(Context context) {
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    static public boolean isMetered(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        if (null == connectivityManager) {
            return true;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            return isActiveNetworkMetered(connectivityManager);
        } else {
            return connectivityManager.isActiveNetworkMetered();
        }
    }

    static public boolean internetAccessPresent() {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(CONNECTIVITY_CHECK_URL).openConnection();
            connection.setInstanceFollowRedirects(false);
            return connection.getResponseCode() == EXPECTED_HTTP_RESPONSE_CODE;
        } catch (IOException e) {
            return false;
        }
    }

    static private boolean isActiveNetworkMetered(ConnectivityManager connectivityManager) {
        final NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info == null) {
            // err on side of caution
            return true;
        }
        final int type = info.getType();
        switch (type) {
            case TYPE_MOBILE:
            case TYPE_MOBILE_DUN:
            case TYPE_MOBILE_HIPRI:
            case TYPE_MOBILE_MMS:
            case TYPE_MOBILE_SUPL:
            case TYPE_WIMAX:
                return true;
            case TYPE_WIFI:
                return false;
            default:
                // err on side of caution
                return true;
        }
    }
}
