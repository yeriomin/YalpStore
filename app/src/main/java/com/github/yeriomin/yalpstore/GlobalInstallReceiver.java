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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.github.yeriomin.yalpstore.model.App;
import com.github.yeriomin.yalpstore.notification.NotificationManagerWrapper;
import com.github.yeriomin.yalpstore.task.InstalledAppsTask;

import java.io.File;

public class GlobalInstallReceiver extends BroadcastReceiver {

    static public final String ACTION_INSTALL_UI_UPDATE = "ACTION_INSTALL_UI_UPDATE";
    static public final String ACTION_PACKAGE_REPLACED_NON_SYSTEM = "ACTION_PACKAGE_REPLACED_NON_SYSTEM";
    static public final String ACTION_PACKAGE_INSTALLATION_FAILED = "ACTION_PACKAGE_INSTALLATION_FAILED";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (TextUtils.isEmpty(intent.getAction()) || null == intent.getData() || TextUtils.isEmpty(intent.getData().getSchemeSpecificPart())) {
            return;
        }
        String action = intent.getAction();
        String packageName = intent.getData().getSchemeSpecificPart();
        Log.i(getClass().getSimpleName(), "Finished installation (" + action + ") of " + packageName);
        boolean actionIsInstall = actionIsInstall(action);
        updateInstalledAppsList(context, packageName, actionIsInstall);
        context.sendBroadcast(new Intent(ACTION_INSTALL_UI_UPDATE).putExtra(Intent.EXTRA_PACKAGE_NAME, packageName));
        ((YalpStoreApplication) context.getApplicationContext()).removePendingUpdate(packageName, actionIsInstall);
        if (!actionIsInstall) {
            return;
        }
        BlackWhiteListManager manager = new BlackWhiteListManager(context);
        if (wasInstalled(context, packageName) && needToAutoWhitelist(context) && !manager.isBlack()) {
            Log.i(getClass().getSimpleName(), "Whitelisting " + packageName);
            manager.add(packageName);
        }
        App app = YalpStoreApplication.installedPackages.get(packageName);
        if (needToRemoveApk(context)) {
            removeApk(context, app);
        }
        if (installationMethodIsDefault(context)) {
            new NotificationManagerWrapper(context).cancel(app.getDisplayName());
        }
    }

    static private void updateInstalledAppsList(Context context, String packageName, boolean installed) {
        if (installed) {
            YalpStoreApplication.installedPackages.put(packageName, InstalledAppsTask.getInstalledApp(context.getPackageManager(), packageName));
        } else {
            YalpStoreApplication.installedPackages.remove(packageName);
        }
        context.sendBroadcast(new Intent(ACTION_INSTALL_UI_UPDATE));
    }

    static public boolean actionIsInstall(String action) {
        return action.equals(Intent.ACTION_PACKAGE_INSTALL)
            || action.equals(Intent.ACTION_PACKAGE_ADDED)
            || action.equals(Intent.ACTION_PACKAGE_REPLACED)
            || action.equals(ACTION_PACKAGE_REPLACED_NON_SYSTEM)
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

    static private boolean installationMethodIsDefault(Context context) {
        return PreferenceUtil.getString(context, PreferenceUtil.INSTALLATION_METHOD_DEFAULT).equals(PreferenceUtil.INSTALLATION_METHOD_DEFAULT);
    }
}
