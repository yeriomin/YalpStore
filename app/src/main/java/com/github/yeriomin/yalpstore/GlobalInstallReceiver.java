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

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.github.yeriomin.yalpstore.model.App;
import com.github.yeriomin.yalpstore.notification.NotificationManagerWrapper;

import java.io.File;

public class GlobalInstallReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (!expectedAction(action) || null == intent.getData()) {
            return;
        }
        String packageName = intent.getData().getSchemeSpecificPart();
        Log.i(getClass().getSimpleName(), "Finished installation of " + packageName);
        if (TextUtils.isEmpty(packageName)) {
            return;
        }
        boolean actionIsInstall = actionIsInstall(intent);
        if (null != DetailsActivity.app && packageName.equals(DetailsActivity.app.getPackageName())) {
            updateDetails(actionIsInstall);
        }
        ((YalpStoreApplication) context.getApplicationContext()).removePendingUpdate(packageName, actionIsInstall);
        if (!actionIsInstall) {
            return;
        }
        BlackWhiteListManager manager = new BlackWhiteListManager(context);
        if (wasInstalled(context, packageName) && needToAutoWhitelist(context) && !manager.isBlack()) {
            Log.i(getClass().getSimpleName(), "Whitelisting " + packageName);
            manager.add(packageName);
        }
        App app = getApp(context, packageName);
        if (needToRemoveApk(context)) {
            removeApk(context, app);
        }
        if (installationMethodIsDefault(context)) {
            new NotificationManagerWrapper(context).cancel(app.getDisplayName());
        }
    }

    static public void updateDetails(boolean installed) {
        if (installed) {
            DetailsActivity.app.getPackageInfo().versionCode = DetailsActivity.app.getVersionCode();
            DetailsActivity.app.setInstalled(true);
        } else {
            DetailsActivity.app.getPackageInfo().versionCode = 0;
            DetailsActivity.app.setInstalled(false);
        }
    }

    static public boolean actionIsInstall(Intent intent) {
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
        return PreferenceUtil.getBoolean(context, PreferenceUtil.PREFERENCE_DELETE_APK_AFTER_INSTALL)
            || PreferenceUtil.getBoolean(context, PreferenceUtil.PREFERENCE_DOWNLOAD_INTERNAL_STORAGE)
        ;
    }

    static private boolean needToAutoWhitelist(Context context) {
        return PreferenceUtil.getBoolean(context, PreferenceUtil.PREFERENCE_AUTO_WHITELIST);
    }

    static private App getApp(Context context, String packageName) {
        App app = new App();
        PackageManager pm = context.getPackageManager();
        try {
            app = new App(pm.getPackageInfo(packageName, PackageManager.GET_META_DATA));
            app.setDisplayName(pm.getApplicationLabel(app.getPackageInfo().applicationInfo).toString());
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(GlobalInstallReceiver.class.getSimpleName(), "Install broadcast received, but package " + packageName + " not found");
        }
        return app;
    }

    static private boolean wasInstalled(Context context, String packageName) {
        return InstallationState.isInstalled(packageName)
            || (installationMethodIsDefault(context)
                && DownloadState.get(packageName).isEverythingFinished()
            )
        ;
    }

    static private void removeApk(Context context, App app) {
        File apkPath = Paths.getApkPath(context, app.getPackageName(), app.getVersionCode());
        boolean deleted = apkPath.delete();
        Log.i(GlobalInstallReceiver.class.getSimpleName(), "Removed " + apkPath + " successfully: " + deleted);
    }

    static boolean installationMethodIsDefault(Context context) {
        return PreferenceUtil.getString(context, PreferenceUtil.INSTALLATION_METHOD_DEFAULT).equals(PreferenceUtil.INSTALLATION_METHOD_DEFAULT);
    }
}
