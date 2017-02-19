package com.github.yeriomin.yalpstore;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.util.Log;

import com.github.yeriomin.yalpstore.model.App;

import java.io.File;

public class InstalledReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (!action.equals(Intent.ACTION_PACKAGE_INSTALL)
            && !action.equals(Intent.ACTION_PACKAGE_ADDED)
            && !action.equals(Intent.ACTION_PACKAGE_REPLACED)
            && !action.equals(Intent.ACTION_PACKAGE_REMOVED)
            && !action.equals(Intent.ACTION_PACKAGE_FULLY_REMOVED)
        ) {
            return;
        }
        UpdatableAppsActivity.setNeedsUpdate(true);
        if (needToRemoveApk(context) && action.equals(Intent.ACTION_PACKAGE_ADDED)) {
            App app = getApp(context, intent.getData().getSchemeSpecificPart());
            File apkPath = PlayStoreApiWrapper.getApkPath(app);
            boolean deleted = apkPath.delete();
            Log.i(getClass().getName(), "Removed " + apkPath + " successfully: " + deleted);
        }
    }

    static private boolean needToRemoveApk(Context context) {
        return PreferenceManager
            .getDefaultSharedPreferences(context)
            .getBoolean(PreferenceActivity.PREFERENCE_DELETE_APK_AFTER_INSTALL, false)
            ;
    }

    static private App getApp(Context context, String packageName) {
        App app = new App();
        PackageManager pm = context.getPackageManager();
        try {
            app.setPackageInfo(pm.getPackageInfo(packageName, PackageManager.GET_META_DATA));
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(InstalledReceiver.class.getName(), "Install broadcast received, but package " + packageName + " not found");
        }
        return app;
    }
}
