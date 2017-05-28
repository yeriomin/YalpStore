package com.github.yeriomin.yalpstore;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

import eu.chainfire.libsuperuser.Shell;

public class DownloadManagerFactory {

    private static final String DOWNLOAD_MANAGER_PACKAGE_NAME = "com.android.providers.downloads";

    static public DownloadManagerInterface get(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD
            || !nativeDownloadManagerEnabled(context)
            || nougatVpn()
        ) {
            Log.i(DownloadManagerFactory.class.getName(), "DownloadManager unavailable - using a fallback");
            return new DownloadManagerFake(context);
        } else {
            Log.i(DownloadManagerFactory.class.getName(), "DownloadManager is found and is going to be used");
            return new DownloadManagerAdapter(context);
        }
    }

    static private boolean nativeDownloadManagerEnabled(Context context) {
        int state = PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
        try {
            state = context.getPackageManager().getApplicationEnabledSetting(DOWNLOAD_MANAGER_PACKAGE_NAME);
        } catch (Throwable e) {
            Log.w(DownloadManagerFactory.class.getName(), "Could not check DownloadManager status: " + e.getMessage());
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
            return !(state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER
                || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED
            );
        } else {
            return !(state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER
            );
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    static private boolean nougatVpn() {
        if (Build.VERSION.SDK_INT != Build.VERSION_CODES.N && Build.VERSION.SDK_INT != Build.VERSION_CODES.N_MR1) {
            return false;
        }
        for (String line: Shell.SH.run("ls -1 /sys/class/net")) {
            String networkName = line.trim();
            if (networkName.startsWith("tun") || networkName.startsWith("ppp")) {
                Log.i(DownloadManagerFactory.class.getName(), "VPN seems to be on: " + networkName);
                return true;
            }
        }
        return false;
    }
}
