package com.github.yeriomin.yalpstore;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;

import eu.chainfire.libsuperuser.Shell;

import static android.content.Context.CONNECTIVITY_SERVICE;
import static android.net.ConnectivityManager.TYPE_MOBILE;
import static android.net.ConnectivityManager.TYPE_MOBILE_DUN;
import static android.net.ConnectivityManager.TYPE_MOBILE_HIPRI;
import static android.net.ConnectivityManager.TYPE_MOBILE_MMS;
import static android.net.ConnectivityManager.TYPE_MOBILE_SUPL;
import static android.net.ConnectivityManager.TYPE_WIFI;
import static android.net.ConnectivityManager.TYPE_WIMAX;

public class NetworkState {

    static public boolean isVpn() {
        for (String line: Shell.SH.run("ls -1 /sys/class/net")) {
            String networkName = line.trim();
            if (networkName.startsWith("tun") || networkName.startsWith("ppp")) {
                Log.i(NetworkState.class.getName(), "VPN seems to be on: " + networkName);
                return true;
            }
        }
        return false;
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
