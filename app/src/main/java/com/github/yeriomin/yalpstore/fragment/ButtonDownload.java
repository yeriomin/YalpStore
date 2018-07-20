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

package com.github.yeriomin.yalpstore.fragment;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.github.yeriomin.playstoreapi.AndroidAppDeliveryData;
import com.github.yeriomin.yalpstore.BuildConfig;
import com.github.yeriomin.yalpstore.ContextUtil;
import com.github.yeriomin.yalpstore.DownloadManagerAbstract;
import com.github.yeriomin.yalpstore.DownloadState;
import com.github.yeriomin.yalpstore.Downloader;
import com.github.yeriomin.yalpstore.ManualDownloadActivity;
import com.github.yeriomin.yalpstore.Paths;
import com.github.yeriomin.yalpstore.PlayStoreApiAuthenticator;
import com.github.yeriomin.yalpstore.PreferenceUtil;
import com.github.yeriomin.yalpstore.R;
import com.github.yeriomin.yalpstore.YalpStoreActivity;
import com.github.yeriomin.yalpstore.YalpStorePermissionManager;
import com.github.yeriomin.yalpstore.model.App;
import com.github.yeriomin.yalpstore.selfupdate.UpdaterFactory;
import com.github.yeriomin.yalpstore.task.playstore.DownloadLinkTask;
import com.github.yeriomin.yalpstore.task.playstore.PurchaseTask;

import java.io.File;

import static com.github.yeriomin.yalpstore.DownloadState.TriggeredBy.DOWNLOAD_BUTTON;
import static com.github.yeriomin.yalpstore.DownloadState.TriggeredBy.MANUAL_DOWNLOAD_BUTTON;

public class ButtonDownload extends Button {

    public ButtonDownload(YalpStoreActivity activity, App app) {
        super(activity, app);
    }

    @Override
    protected View getButton() {
        return activity.findViewById(R.id.download);
    }

    @Override
    public boolean shouldBeVisible() {
        File apk = Paths.getApkPath(activity, app.getPackageName(), app.getVersionCode());
        return (!apk.exists()
                || apk.length() != app.getSize()
                || !DownloadState.get(app.getPackageName()).isEverythingSuccessful()
            )
            && (app.isFree() || !PreferenceUtil.getBoolean(activity, PlayStoreApiAuthenticator.PREFERENCE_APP_PROVIDED_EMAIL))
            && (app.isInPlayStore() || app.getPackageName().equals(BuildConfig.APPLICATION_ID))
            && (getInstalledVersionCode() != app.getVersionCode() || activity instanceof ManualDownloadActivity)
        ;
    }

    @Override
    protected void onButtonClick(View v) {
        checkAndDownload();
    }

    public void checkAndDownload() {
        YalpStorePermissionManager permissionManager = new YalpStorePermissionManager(activity);
        if (app.getVersionCode() == 0 && !(activity instanceof ManualDownloadActivity)) {
            activity.startActivity(new Intent(activity, ManualDownloadActivity.class));
        } else if (permissionManager.checkPermission()) {
            Log.i(getClass().getSimpleName(), "Write permission granted");
            download();
            View buttonCancel = activity.findViewById(R.id.cancel);
            if (null != buttonCancel) {
                buttonCancel.setVisibility(View.VISIBLE);
            }
        } else {
            permissionManager.requestPermission();
        }
    }

    @Override
    public void draw() {
        super.draw();
        DownloadState state = DownloadState.get(app.getPackageName());
        if (Paths.getApkPath(activity, app.getPackageName(), app.getVersionCode()).exists()
            && !state.isEverythingSuccessful()
        ) {
            disable(R.string.details_downloading);
        }
        if (null != button) {
            button.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    DownloadLinkTask task = new DownloadLinkTask();
                    task.setApp(app);
                    task.setContext(activity);
                    task.execute();
                    return true;
                }
            });
        }
    }

    public void download() {
        if (app.getPackageName().equals(BuildConfig.APPLICATION_ID)) {
            new Downloader(activity).download(
                app,
                AndroidAppDeliveryData.newBuilder().setDownloadUrl(UpdaterFactory.get(activity).getUrlString(app.getVersionCode())).build()
            );
        } else {
            if (new YalpStorePermissionManager(activity).checkPermission() && prepareDownloadsDir()) {
                getPurchaseTask().execute();
            } else {
                ContextUtil.toast(this.activity.getApplicationContext(), R.string.error_downloads_directory_not_writable);
            }
        }
    }

    private boolean prepareDownloadsDir() {
        File dir = Paths.getYalpPath(activity);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir.exists() && dir.isDirectory() && dir.canWrite();
    }

    private LocalPurchaseTask getPurchaseTask() {
        LocalPurchaseTask purchaseTask = new LocalPurchaseTask();
        purchaseTask.setFragment(this);
        purchaseTask.setApp(app);
        purchaseTask.setContext(activity);
        purchaseTask.setTriggeredBy(activity instanceof ManualDownloadActivity ? MANUAL_DOWNLOAD_BUTTON : DOWNLOAD_BUTTON);
        purchaseTask.setProgressIndicator(activity.findViewById(R.id.progress));
        return purchaseTask;
    }

    private int getInstalledVersionCode() {
        try {
            return activity.getPackageManager().getPackageInfo(app.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            return 0;
        }
    }

    static class LocalPurchaseTask extends PurchaseTask {

        private ButtonDownload fragment;

        public LocalPurchaseTask setFragment(ButtonDownload fragment) {
            this.fragment = fragment;
            return this;
        }

        @Override
        public LocalPurchaseTask clone() {
            LocalPurchaseTask task = new LocalPurchaseTask();
            task.setTriggeredBy(triggeredBy);
            task.setApp(app);
            task.setErrorView(errorView);
            task.setContext(context);
            task.setProgressIndicator(progressIndicator);
            task.setFragment(fragment);
            return task;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            fragment.disable(R.string.details_downloading);
        }

        @Override
        protected void onPostExecute(AndroidAppDeliveryData deliveryData) {
            super.onPostExecute(deliveryData);
            if (!success()) {
                fragment.draw();
                String restriction = DownloadManagerAbstract.getRestrictionString(context, app.getRestriction());
                if (!TextUtils.isEmpty(restriction)) {
                    ContextUtil.toastLong(context, restriction);
                    Log.i(getClass().getSimpleName(), "No download link returned, app restriction is " + app.getRestriction());
                }
            }
        }
    }
}
