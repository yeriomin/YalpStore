package com.github.yeriomin.yalpstore;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.widget.Toast;

public class PrivilegeChecker {

    static public boolean check(Activity activity) {
        PackageManager pm = activity.getPackageManager();
        boolean privileged = pm.checkPermission(Manifest.permission.INSTALL_PACKAGES, BuildConfig.APPLICATION_ID) == PackageManager.PERMISSION_GRANTED
            && pm.checkPermission(Manifest.permission.DELETE_PACKAGES, BuildConfig.APPLICATION_ID) == PackageManager.PERMISSION_GRANTED
        ;
        if (!privileged) {
            Toast.makeText(activity, R.string.pref_not_privileged, Toast.LENGTH_LONG).show();
        }
        return privileged;
    }
}
