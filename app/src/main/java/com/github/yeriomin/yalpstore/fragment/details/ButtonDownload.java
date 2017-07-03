package com.github.yeriomin.yalpstore.fragment.details;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.yeriomin.yalpstore.DetailsActivity;
import com.github.yeriomin.yalpstore.DownloadState;
import com.github.yeriomin.yalpstore.ManualDownloadActivity;
import com.github.yeriomin.yalpstore.OnDownloadProgressListener;
import com.github.yeriomin.yalpstore.Paths;
import com.github.yeriomin.yalpstore.PurchaseTask;
import com.github.yeriomin.yalpstore.R;
import com.github.yeriomin.yalpstore.model.App;
import com.github.yeriomin.yalpstore.notification.CancelDownloadService;

import java.io.File;

import static com.github.yeriomin.yalpstore.DownloadState.TriggeredBy.DOWNLOAD_BUTTON;
import static com.github.yeriomin.yalpstore.DownloadState.TriggeredBy.MANUAL_DOWNLOAD_BUTTON;

public class ButtonDownload extends Button {

    private ImageButton cancelButton;

    public ButtonDownload(final DetailsActivity activity, final App app) {
        super(activity, app);
        cancelButton = (ImageButton) activity.findViewById(R.id.cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentCancel = new Intent(activity.getApplicationContext(), CancelDownloadService.class);
                intentCancel.putExtra(CancelDownloadService.PACKAGE_NAME, app.getPackageName());
                activity.startService(intentCancel);
                v.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected android.widget.Button getButton() {
        return (android.widget.Button) activity.findViewById(R.id.download);
    }

    @Override
    protected boolean shouldBeVisible() {
        return (!Paths.getApkPath(app.getPackageName(), app.getVersionCode()).exists()
                || !DownloadState.get(app.getPackageName()).isEverythingSuccessful()
            )
            && app.isInPlayStore()
            && (getInstalledVersionCode() != app.getVersionCode() || activity instanceof ManualDownloadActivity)
        ;
    }

    @Override
    protected void onButtonClick(View v) {
        if (app.getVersionCode() == 0 && !(activity instanceof ManualDownloadActivity)) {
            activity.startActivity(new Intent(activity, ManualDownloadActivity.class));
        } else if (checkPermission()) {
            download();
            cancelButton.setVisibility(View.VISIBLE);
        } else {
            requestPermission();
        }
    }

    @Override
    public void draw() {
        super.draw();
        if (Paths.getApkPath(app.getPackageName(), app.getVersionCode()).exists()
            && !DownloadState.get(app.getPackageName()).isEverythingSuccessful()
        ) {
            disableButton(R.id.download, R.string.details_downloading);
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
        File dir = Paths.getApkPath(app.getPackageName(), app.getVersionCode()).getParentFile();
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
        ProgressBar progressBar = (ProgressBar) activity.findViewById(R.id.download_progress);
        progressBar.setVisibility(View.VISIBLE);
        purchaseTask.setOnDownloadProgressListener(new OnDownloadProgressListener(progressBar, DownloadState.get(app.getPackageName())));
        purchaseTask.setApp(app);
        purchaseTask.setContext(activity);
        purchaseTask.setTriggeredBy(activity instanceof ManualDownloadActivity ? MANUAL_DOWNLOAD_BUTTON : DOWNLOAD_BUTTON);
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

    private int getInstalledVersionCode() {
        try {
            return activity.getPackageManager().getPackageInfo(app.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            return 0;
        }
    }
}
