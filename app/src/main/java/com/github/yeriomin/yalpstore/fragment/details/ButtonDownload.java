package com.github.yeriomin.yalpstore.fragment.details;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.github.yeriomin.playstoreapi.AndroidAppDeliveryData;
import com.github.yeriomin.yalpstore.BuildConfig;
import com.github.yeriomin.yalpstore.ContextUtil;
import com.github.yeriomin.yalpstore.DownloadProgressBarUpdater;
import com.github.yeriomin.yalpstore.DownloadState;
import com.github.yeriomin.yalpstore.Downloader;
import com.github.yeriomin.yalpstore.ManualDownloadActivity;
import com.github.yeriomin.yalpstore.Paths;
import com.github.yeriomin.yalpstore.R;
import com.github.yeriomin.yalpstore.YalpStoreActivity;
import com.github.yeriomin.yalpstore.YalpStorePermissionManager;
import com.github.yeriomin.yalpstore.model.App;
import com.github.yeriomin.yalpstore.selfupdate.UpdaterFactory;
import com.github.yeriomin.yalpstore.task.playstore.PurchaseTask;

import java.io.File;

import static com.github.yeriomin.yalpstore.DownloadState.TriggeredBy.DOWNLOAD_BUTTON;
import static com.github.yeriomin.yalpstore.DownloadState.TriggeredBy.MANUAL_DOWNLOAD_BUTTON;

public class ButtonDownload extends Button {

    public ButtonDownload(YalpStoreActivity activity, App app) {
        super(activity, app);
    }

    @Override
    protected android.widget.Button getButton() {
        return (android.widget.Button) activity.findViewById(R.id.download);
    }

    @Override
    public boolean shouldBeVisible() {
        return (!Paths.getApkPath(activity, app.getPackageName(), app.getVersionCode()).exists()
                || !DownloadState.get(app.getPackageName()).isEverythingSuccessful()
            )
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
            ProgressBar progressBar = activity.findViewById(R.id.download_progress);
            if (null != progressBar) {
                new DownloadProgressBarUpdater(app.getPackageName(), progressBar).execute(PurchaseTask.UPDATE_INTERVAL);
            }
        }
    }

    public void download() {
        if (app.getPackageName().equals(BuildConfig.APPLICATION_ID)) {
            new Downloader(button.getContext()).download(
                app,
                AndroidAppDeliveryData.newBuilder().setDownloadUrl(UpdaterFactory.get(activity).getUrlString(app.getVersionCode())).build()
            );
        } else {
            boolean writePermission = new YalpStorePermissionManager(activity).checkPermission();
            Log.i(getClass().getSimpleName(), "Write permission granted - " + writePermission);
            if (writePermission && prepareDownloadsDir()) {
                getPurchaseTask().execute();
            } else {
                File dir = Paths.getYalpPath(activity);
                Log.i(getClass().getSimpleName(), dir.getAbsolutePath() + " exists=" + dir.exists() + ", isDirectory=" + dir.isDirectory() + ", writable=" + dir.canWrite());
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
        ProgressBar progressBar = activity.findViewById(R.id.download_progress);
        if (null != progressBar) {
            purchaseTask.setDownloadProgressBarUpdater(new DownloadProgressBarUpdater(app.getPackageName(), progressBar));
        }
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
            task.setDownloadProgressBarUpdater(progressBarUpdater);
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
                if (null != getRestrictionString()) {
                    ContextUtil.toastLong(context, getRestrictionString());
                    Log.i(getClass().getSimpleName(), "No download link returned, app restriction is " + app.getRestriction());
                }
            }
        }
    }
}
