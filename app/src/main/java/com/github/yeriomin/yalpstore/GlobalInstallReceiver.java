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
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.github.yeriomin.yalpstore.model.App;
import com.github.yeriomin.yalpstore.model.Event;
import com.github.yeriomin.yalpstore.model.EventDao;
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
        insertEvent(context, prepareEvent(context, packageName, action));
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

    static private Event prepareEvent(Context context, String packageName, String action) {
        Event event = new Event();
        event.setPackageName(packageName);
        switch (action) {
            case ACTION_PACKAGE_INSTALLATION_FAILED:
                event.setType(Event.TYPE.INSTALLATION);
                event.setMessage(context.getString(R.string.details_install_failure));
                break;
            case Intent.ACTION_PACKAGE_REMOVED:
            case Intent.ACTION_PACKAGE_FULLY_REMOVED:
                event.setType(Event.TYPE.REMOVAL);
                event.setMessage(context.getString(R.string.uninstalled));
                break;
            case Intent.ACTION_PACKAGE_INSTALL:
            case Intent.ACTION_PACKAGE_ADDED:
            case Intent.ACTION_PACKAGE_REPLACED:
            case ACTION_PACKAGE_REPLACED_NON_SYSTEM:
                Event pendingEvent = getPendingEvent(context, packageName);
                if (null == pendingEvent || null == pendingEvent.getType()) {
                    fillIncompleteEvent(context, action, event);
                } else if ((!pendingEvent.getType().equals(Event.TYPE.UPDATE) && action.equals(Intent.ACTION_PACKAGE_REPLACED))
                    || (!pendingEvent.getType().equals(Event.TYPE.INSTALLATION) && (action.equals(Intent.ACTION_PACKAGE_ADDED) || action.equals(Intent.ACTION_PACKAGE_INSTALL)))
                ) {
                    // During update three broadcasts are sent in sequence: remove, install, update
                    // If the action does not match pending event type, we do nothing
                    return null;
                } else {
                    event = pendingEvent;
                }
                break;
        }
        return event;
    }

    static private void updateInstalledAppsList(Context context, String packageName, boolean installed) {
        if (installed) {
            YalpStoreApplication.installedPackages.put(packageName, InstalledAppsTask.getInstalledApp(context.getPackageManager(), packageName));
        } else {
            YalpStoreApplication.installedPackages.remove(packageName);
        }
    }

    static private Event getPendingEvent(Context context, String packageName) {
        SQLiteDatabase db = new SqliteHelper(context).getReadableDatabase();
        EventDao dao = new EventDao(db);
        Event event = dao.getPendingEvent(packageName);
        db.close();
        return event;
    }

    static private void fillIncompleteEvent(Context context, String action, Event event) {
        boolean installation = action.equals(Intent.ACTION_PACKAGE_ADDED) || action.equals(Intent.ACTION_PACKAGE_INSTALL);
        event.setType(installation ? Event.TYPE.INSTALLATION : Event.TYPE.UPDATE);
        App oldApp = YalpStoreApplication.installedPackages.get(event.getPackageName());
        App newApp = InstalledAppsTask.getInstalledApp(context.getPackageManager(), event.getPackageName());
        event.setMessage(
            installation
                ? context.getString(R.string.details_installed)
                : context.getString(
                    R.string.updated_from_to,
                    null == oldApp ? "?" : oldApp.getVersionName(),
                    null == oldApp ? 0 : oldApp.getVersionCode(),
                    newApp.getVersionName(),
                    newApp.getVersionCode()
                )
        );
    }

    static private void insertEvent(Context context, Event event) {
        if (null == event) {
            return;
        }
        SQLiteDatabase db = new SqliteHelper(context).getWritableDatabase();
        EventDao dao = new EventDao(db);
        if (event.isPending()) {
            dao.confirmPendingEvent(event);
        } else {
            dao.insert(event);
        }
        if (event.getType().equals(Event.TYPE.UPDATE)) {
            // During update three broadcasts are sent in sequence: remove, install, update
            // This removes intermediate remove and install
            dao.cleanupUpdates(event.getPackageName());
        }
        db.close();
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
