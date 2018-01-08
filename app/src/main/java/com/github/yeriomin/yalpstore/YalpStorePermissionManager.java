package com.github.yeriomin.yalpstore;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import java.lang.ref.WeakReference;

public class YalpStorePermissionManager {

    private static final int PERMISSIONS_REQUEST_CODE = 384;

    private WeakReference<Activity> activityRef = new WeakReference<>(null);

    public YalpStorePermissionManager(Activity activity) {
        this.activityRef = new WeakReference<>(activity);
    }

    static public boolean isGranted(int requestCode, String permissions[], int[] grantResults) {
        return requestCode == PERMISSIONS_REQUEST_CODE
            && grantResults.length > 0
            && grantResults[0] == PackageManager.PERMISSION_GRANTED
        ;
    }

    public boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && null != activityRef.get()) {
            Log.i(getClass().getSimpleName(), "Checking if write permission is granted");
            return activityRef.get().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    public void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && null != activityRef.get()) {
            Log.i(getClass().getSimpleName(), "Requesting the write permission");
            activityRef.get().requestPermissions(
                new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE },
                PERMISSIONS_REQUEST_CODE
            );
        }
    }
}
