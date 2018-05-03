package com.dragons.aurora.downloader;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import com.dragons.aurora.NetworkState;

public class DownloadManagerFactory {

    private static final String DOWNLOAD_MANAGER_PACKAGE_NAME = "com.android.providers.downloads";

    static public DownloadManagerInterface get(Context context) {
        if (!nativeDownloadManagerEnabled(context) || nougatVpn(context)
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
        if (Build.VERSION.SDK_INT != Build.VERSION_CODES.N && Build.VERSION.SDK_INT != Build.VERSION_CODES.N_MR1) {
            return false;
        }
        return NetworkState.isVpn(context);
    }
}
