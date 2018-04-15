package in.dragons.galaxy.fragment.details;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.dragons.aurora.playstoreapiv2.AndroidAppDeliveryData;
import com.percolate.caffeine.ViewUtils;

import java.io.File;

import in.dragons.galaxy.BuildConfig;
import in.dragons.galaxy.ContextUtil;
import in.dragons.galaxy.GalaxyPermissionManager;
import in.dragons.galaxy.NumberProgressBar;
import in.dragons.galaxy.Paths;
import in.dragons.galaxy.R;
import in.dragons.galaxy.activities.GalaxyActivity;
import in.dragons.galaxy.activities.ManualDownloadActivity;
import in.dragons.galaxy.downloader.DownloadProgressBarUpdater;
import in.dragons.galaxy.downloader.DownloadState;
import in.dragons.galaxy.model.App;
import in.dragons.galaxy.task.playstore.PurchaseTask;

import static in.dragons.galaxy.downloader.DownloadState.TriggeredBy.DOWNLOAD_BUTTON;
import static in.dragons.galaxy.downloader.DownloadState.TriggeredBy.MANUAL_DOWNLOAD_BUTTON;

public class ButtonDownload extends Button {

    private NumberProgressBar progressBar;

    public ButtonDownload(GalaxyActivity activity, App app) {
        super(activity, app);
    }

    @Override
    protected android.widget.Button getButton() {
        if (app.getPrice() != null && !app.isFree()) {
            setText(R.id.download, R.string.details_purchase);
            setToPlayStore();
            return (android.widget.Button) activity.findViewById(R.id.download);
        } else
            return (android.widget.Button) activity.findViewById(R.id.download);
    }

    private void setToPlayStore() {
        android.widget.Button toPlayStore = activity.findViewById(R.id.showInPlayStore);
        toPlayStore.setVisibility(View.VISIBLE);
        toPlayStore.setOnClickListener(v -> {
            Toast.makeText(activity, R.string.warn_app_purchase, Toast.LENGTH_SHORT).show();
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + app.getPackageName())));
        });
    }

    @Override
    public boolean shouldBeVisible() {
        File apk = Paths.getApkPath(activity, app.getPackageName(), app.getVersionCode());
        return (!apk.exists()
                || apk.length() != app.getSize()
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
        View buttonDownload = activity.findViewById(R.id.download);
        if (null != buttonDownload) buttonDownload.setVisibility(View.GONE);
        View buttonCancel = activity.findViewById(R.id.cancel);
        GalaxyPermissionManager permissionManager = new GalaxyPermissionManager(activity);
        if (app.getVersionCode() == 0 && !(activity instanceof ManualDownloadActivity)) {
            activity.startActivity(new Intent(activity, ManualDownloadActivity.class));
        } else if (permissionManager.checkPermission()) {
            Log.i(getClass().getSimpleName(), "Write permission granted");
            download();
            if (null != buttonCancel) {
                buttonCancel.setVisibility(View.VISIBLE);
            }
        } else {
            permissionManager.requestPermission();
            button.setVisibility(View.GONE);
            buttonCancel.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void draw() {
        super.draw();
        DownloadState state = DownloadState.get(app.getPackageName());
        if (Paths.getApkPath(activity, app.getPackageName(), app.getVersionCode()).exists()
                && !state.isEverythingSuccessful()
                ) {
            progressBar = ViewUtils.findViewById(activity, R.id.download_progress);
            if (null != progressBar) {
                new DownloadProgressBarUpdater(app.getPackageName(), progressBar).execute(PurchaseTask.UPDATE_INTERVAL);
            }
        }
    }

    public void download() {
        boolean writePermission = new GalaxyPermissionManager(activity).checkPermission();
        Log.i(getClass().getSimpleName(), "Write permission granted - " + writePermission);
        if (writePermission && prepareDownloadsDir()) {
            getPurchaseTask().execute();
        } else {
            File dir = Paths.getDownloadPath(activity);
            Log.i(getClass().getSimpleName(), dir.getAbsolutePath() + " exists=" + dir.exists() + ", isDirectory=" + dir.isDirectory() + ", writable=" + dir.canWrite());
            ContextUtil.toast(this.activity.getApplicationContext(), R.string.error_downloads_directory_not_writable);
        }
    }

    private boolean prepareDownloadsDir() {
        File dir = Paths.getDownloadPath(activity);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir.exists() && dir.isDirectory() && dir.canWrite();
    }

    private LocalPurchaseTask getPurchaseTask() {
        LocalPurchaseTask purchaseTask = new LocalPurchaseTask();
        purchaseTask.setFragment(this);
        progressBar = ViewUtils.findViewById(activity, R.id.download_progress);
        if (null != progressBar) {
            purchaseTask.setDownloadProgressBarUpdater(new DownloadProgressBarUpdater(app.getPackageName(), progressBar));
        }
        purchaseTask.setApp(app);
        purchaseTask.setContext(activity);
        purchaseTask.setTriggeredBy(activity instanceof ManualDownloadActivity ? MANUAL_DOWNLOAD_BUTTON : DOWNLOAD_BUTTON);
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

        private ButtonDownload buttonDownload;

        public LocalPurchaseTask setFragment(ButtonDownload fragment) {
            this.buttonDownload = fragment;
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
            task.setFragment(buttonDownload);
            return task;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(AndroidAppDeliveryData deliveryData) {
            super.onPostExecute(deliveryData);
            if (!success()) {
                buttonDownload.draw();
                if (null != getRestrictionString()) {
                    ContextUtil.toastLong(context, getRestrictionString());
                    Log.i(getClass().getSimpleName(), "No download link returned, app restriction is " + app.getRestriction());
                }
            }
        }
    }
}
