package com.github.yeriomin.yalpstore;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;

public final class DownloadManagerChecker {

    private static final String DOWNLOAD_MANAGER_PACKAGE_NAME = "com.android.providers.downloads";

    static public boolean isEnabled(Context context) {
        int state = context.getPackageManager().getApplicationEnabledSetting(DOWNLOAD_MANAGER_PACKAGE_NAME);
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

    static public void showDownloadManagerAppPage(Context context) {
        try {
            showSystemAppPage(context, DOWNLOAD_MANAGER_PACKAGE_NAME);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            showSystemAppPage(context, null);
        }
    }

    static private void showSystemAppPage(Context context, String packageName) {
        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        if (null != packageName) {
            intent.setData(Uri.parse("package:" + packageName));
        }
        context.startActivity(intent);
    }
}