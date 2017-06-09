package com.github.yeriomin.yalpstore.fragment.details;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.view.View;
import android.widget.Toast;

import com.github.yeriomin.yalpstore.DetailsActivity;
import com.github.yeriomin.yalpstore.Downloader;
import com.github.yeriomin.yalpstore.ManualDownloadActivity;
import com.github.yeriomin.yalpstore.PurchaseTask;
import com.github.yeriomin.yalpstore.R;
import com.github.yeriomin.yalpstore.model.App;

import java.io.File;

public class ButtonDownload extends Button {

    public ButtonDownload(DetailsActivity activity, App app) {
        super(activity, app);
    }

    @Override
    protected android.widget.Button getButton() {
        return (android.widget.Button) activity.findViewById(R.id.download);
    }

    @Override
    protected boolean shouldBeVisible() {
        return !Downloader.getApkPath(app.getPackageName(), app.getVersionCode()).exists()
            && app.isInPlayStore()
            && (!isLatestVersion() || activity instanceof ManualDownloadActivity)
        ;
    }

    @Override
    protected void onButtonClick(View v) {
        if (app.getVersionCode() == 0 && !(activity instanceof ManualDownloadActivity)) {
            activity.startActivity(new Intent(activity, ManualDownloadActivity.class));
        } else if (checkPermission()) {
            download();
        } else {
            requestPermission();
        }
    }

    public void download() {
        if (prepareDownloadsDir()) {
            getPurchaseTask().execute();
        } else {
            Toast.makeText(
                this.activity.getApplicationContext(),
                this.activity.getString(R.string.error_downloads_directory_not_writable),
                Toast.LENGTH_LONG
            ).show();
        }
    }

    private boolean prepareDownloadsDir() {
        File dir = Downloader.getApkPath(app.getPackageName(), app.getVersionCode()).getParentFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir.exists() && dir.isDirectory() && dir.canWrite();
    }

    private PurchaseTask getPurchaseTask() {
        PurchaseTask purchaseTask = new PurchaseTask() {
            @Override
            protected void onPostExecute(Throwable e) {
                super.onPostExecute(e);
                if (null == e) {
                    disableButton(R.id.download, R.string.details_downloading);
                }
            }
        };
        purchaseTask.setApp(app);
        purchaseTask.setContext(activity);
        purchaseTask.prepareDialog(
            R.string.dialog_message_purchasing_app,
            R.string.dialog_title_purchasing_app
        );
        return purchaseTask;
    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.requestPermissions(
                new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE },
                DetailsActivity.PERMISSIONS_REQUEST_CODE
            );
        }
    }

    private boolean isLatestVersion() {
        try {
            return activity.getPackageManager().getPackageInfo(app.getPackageName(), 0).versionCode == app.getVersionCode();
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
