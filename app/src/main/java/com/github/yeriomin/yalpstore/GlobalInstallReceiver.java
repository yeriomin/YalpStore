package com.github.yeriomin.yalpstore;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.github.yeriomin.yalpstore.model.App;

import java.io.File;

public class GlobalInstallReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (!expectedAction(action)) {
            return;
        }
        String packageName = intent.getData().getSchemeSpecificPart();
        Log.i(getClass().getName(), "Finished installation of " + packageName);
        updateDetails(intent);
        ((YalpStoreApplication) context.getApplicationContext()).removePendingUpdate(packageName, actionIsInstall(intent));
        if (needToRemoveApk(context) && actionIsInstall(intent)) {
            App app = getApp(context, packageName);
            File apkPath = Paths.getApkPath(context, app.getPackageName(), app.getVersionCode());
            boolean deleted = apkPath.delete();
            Log.i(getClass().getName(), "Removed " + apkPath + " successfully: " + deleted);
        }
    }

    static public void updateDetails(Intent intent) {
        if (null == DetailsActivity.app) {
            return;
        }
        if (actionIsUninstall(intent)) {
            DetailsActivity.app.getPackageInfo().versionCode = 0;
            DetailsActivity.app.setInstalled(false);
        } else if (actionIsInstall(intent)) {
            DetailsActivity.app.getPackageInfo().versionCode = DetailsActivity.app.getVersionCode();
            DetailsActivity.app.setInstalled(true);
        }
    }

    static private boolean actionIsUninstall(Intent intent) {
        return !TextUtils.isEmpty(intent.getAction())
            && (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)
                || intent.getAction().equals(Intent.ACTION_PACKAGE_FULLY_REMOVED)
            )
        ;
    }

    static private boolean actionIsInstall(Intent intent) {
        return !TextUtils.isEmpty(intent.getAction())
            && (intent.getAction().equals(Intent.ACTION_PACKAGE_INSTALL)
                || intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)
                || intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED)
                || intent.getAction().equals(DetailsInstallReceiver.ACTION_PACKAGE_REPLACED_NON_SYSTEM)
            )
        ;
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    static private boolean expectedAction(String action) {
        return action.equals(Intent.ACTION_PACKAGE_INSTALL)
            || action.equals(Intent.ACTION_PACKAGE_ADDED)
            || action.equals(Intent.ACTION_PACKAGE_REPLACED)
            || action.equals(Intent.ACTION_PACKAGE_REMOVED)
            || action.equals(Intent.ACTION_PACKAGE_FULLY_REMOVED)
            || action.equals(DetailsInstallReceiver.ACTION_PACKAGE_REPLACED_NON_SYSTEM)
        ;
    }

    static private boolean needToRemoveApk(Context context) {
        return PreferenceActivity.getBoolean(context, PreferenceActivity.PREFERENCE_DELETE_APK_AFTER_INSTALL);
    }

    static private App getApp(Context context, String packageName) {
        App app = new App();
        PackageManager pm = context.getPackageManager();
        try {
            app = new App(pm.getPackageInfo(packageName, PackageManager.GET_META_DATA));
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(GlobalInstallReceiver.class.getName(), "Install broadcast received, but package " + packageName + " not found");
        }
        return app;
    }
}
