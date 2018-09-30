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

package com.github.yeriomin.yalpstore.task.playstore;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.util.Log;

import com.github.yeriomin.yalpstore.BuildConfig;
import com.github.yeriomin.yalpstore.InstallerAbstract;
import com.github.yeriomin.yalpstore.InstallerFactory;
import com.github.yeriomin.yalpstore.NetworkUtil;
import com.github.yeriomin.yalpstore.Paths;
import com.github.yeriomin.yalpstore.PreferenceUtil;
import com.github.yeriomin.yalpstore.R;
import com.github.yeriomin.yalpstore.SqliteHelper;
import com.github.yeriomin.yalpstore.UpdatableAppsActivity;
import com.github.yeriomin.yalpstore.UpdateAllReceiver;
import com.github.yeriomin.yalpstore.YalpStoreApplication;
import com.github.yeriomin.yalpstore.download.DownloadManager;
import com.github.yeriomin.yalpstore.download.State;
import com.github.yeriomin.yalpstore.model.App;
import com.github.yeriomin.yalpstore.model.Event;
import com.github.yeriomin.yalpstore.model.EventDao;
import com.github.yeriomin.yalpstore.notification.NotificationManagerWrapper;

import java.io.File;
import java.util.List;

public class BackgroundUpdatableAppsTask extends UpdatableAppsTask implements CloneableTask {

    private boolean forceUpdate = false;

    public void setForceUpdate(boolean forceUpdate) {
        this.forceUpdate = forceUpdate;
    }

    @Override
    public CloneableTask clone() {
        BackgroundUpdatableAppsTask task = new BackgroundUpdatableAppsTask();
        task.setForceUpdate(forceUpdate);
        task.setContext(context);
        return task;
    }

    @Override
    protected void onPostExecute(List<App> apps) {
        super.onPostExecute(apps);
        if (!success()) {
            return;
        }
        int updatesCount = this.updatableApps.size();
        Log.i(this.getClass().getName(), "Found updates for " + updatesCount + " apps");
        try {
            insertEvent(updatesCount);
        } catch (Throwable e) {
            // No failure to log an event is important enough to let the app crash
            Log.e(getClass().getSimpleName(), "Could not log event: " + e.getClass().getName() + " " + e.getMessage());
        }
        if (updatesCount == 0) {
            context.sendBroadcast(new Intent(UpdateAllReceiver.ACTION_ALL_UPDATES_COMPLETE), null);
            return;
        }
        if (canUpdate()) {
            process(context, updatableApps);
        } else {
            notifyUpdatesFound(context, updatesCount);
        }
    }

    private void insertEvent(int updatesCount) {
        Event event = new Event();
        event.setType(Event.TYPE.BACKGROUND_UPDATE_CHECK);
        event.setMessage(context.getString(R.string.notification_updates_available_message, updatesCount));
        SQLiteDatabase db = new SqliteHelper(context).getWritableDatabase();
        new EventDao(db).insert(event);
        db.close();
    }

    private boolean canUpdate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
            && !PreferenceUtil.getBoolean(context, PreferenceUtil.PREFERENCE_DOWNLOAD_INTERNAL_STORAGE)
            && context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        ) {
            return false;
        }
        return forceUpdate ||
            (PreferenceUtil.getBoolean(context, PreferenceUtil.PREFERENCE_BACKGROUND_UPDATE_DOWNLOAD)
                && (!PreferenceUtil.getBoolean(context, PreferenceUtil.PREFERENCE_BACKGROUND_UPDATE_WIFI_ONLY)
                    || !NetworkUtil.isMetered(context)
                )
            )
        ;
    }

    private void process(Context context, List<App> apps) {
        boolean canInstallInBackground = PreferenceUtil.canInstallInBackground(context);
        YalpStoreApplication application = (YalpStoreApplication) context.getApplicationContext();
        application.clearPendingUpdates();
        for (App app: apps) {
            if (DownloadManager.isCancelled(app.getPackageName())) {
                Log.i(getClass().getSimpleName(), app.getPackageName() + " cancelled before starting");
                continue;
            }
            application.addPendingUpdate(app.getPackageName());
            File apkPath = Paths.getApkPath(context, app.getPackageName(), app.getVersionCode());
            if (!apkPath.exists()
                || (PreferenceUtil.getBoolean(context, PreferenceUtil.PREFERENCE_DOWNLOAD_INTERNAL_STORAGE)
                    && null == DownloadManager.getApkExpectedHash(app.getPackageName())
                )
            ) {
                apkPath.delete();
                download(context, app);
            } else if (canInstallInBackground) {
                // Not passing context because it might be an activity
                // and we want it to run in background
                InstallerFactory.get(context.getApplicationContext()).verifyAndInstall(app);
            } else {
                notifyDownloadedAlready(app);
                application.removePendingUpdate(app.getPackageName());
            }
        }
    }

    private void download(Context context, App app) {
        Log.i(getClass().getSimpleName(), "Starting download of update for " + app.getPackageName());
        getPurchaseTask(context, app).execute();
    }

    private BackgroundPurchaseTask getPurchaseTask(Context context, App app) {
        BackgroundPurchaseTask task = new BackgroundPurchaseTask();
        task.setApp(app);
        task.setContext(context);
        task.setTriggeredBy(context instanceof Activity
            ? State.TriggeredBy.UPDATE_ALL_BUTTON
            : State.TriggeredBy.SCHEDULED_UPDATE
        );
        return task;
    }

    private void notifyUpdatesFound(Context context, int updatesCount) {
        Intent i = new Intent(context, UpdatableAppsActivity.class);
        i.setAction(Intent.ACTION_VIEW);
        new NotificationManagerWrapper(context).show(
            BuildConfig.APPLICATION_ID,
            i,
            context.getString(R.string.notification_updates_available_title),
            context.getString(R.string.notification_updates_available_message, updatesCount)
        );
    }

    private void notifyDownloadedAlready(App app) {
        new NotificationManagerWrapper(context).show(
            app.getPackageName(),
            InstallerAbstract.getDownloadChecksumServiceIntent(app.getPackageName()),
            app.getDisplayName(),
            context.getString(R.string.notification_download_complete)
        );
    }
}
