package com.github.yeriomin.yalpstore;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.yeriomin.yalpstore.model.App;

import java.io.File;

public class DownloadOrInstallFragment extends DetailsFragment {

    private File apkPath;
    private DetailsDownloadReceiver downloadReceiver;
    private DetailsInstallReceiver installReceiver;

    public DownloadOrInstallFragment(DetailsActivity activity, App app) {
        super(activity, app);
    }

    @Override
    public void draw() {
        apkPath = Downloader.getApkPath(app.getPackageName(), app.getVersionCode());
        drawUninstallButton();
        drawDownloadButton();
    }

    private void drawUninstallButton() {
        Button uninstallButton = (Button) activity.findViewById(R.id.uninstall);
        if (null == uninstallButton) {
            return;
        }
        uninstallButton.setVisibility(app.isInstalled() ? View.VISIBLE : View.GONE);
        uninstallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.startActivity(new Intent(Intent.ACTION_DELETE, Uri.parse("package:" + app.getPackageName())));
            }
        });
    }

    private void drawDownloadButton() {
        Button downloadButton = (Button) activity.findViewById(R.id.download);
        downloadButton.setText(apkPath.exists() ? R.string.details_install : R.string.details_download);
        downloadButton.setEnabled(true);
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (app.getVersionCode() == 0 && !(activity instanceof ManualDownloadActivity)) {
                    activity.startActivity(new Intent(activity, ManualDownloadActivity.class));
                } else if (checkPermission()) {
                    downloadOrInstall();
                } else {
                    requestPermission();
                }
            }
        });
    }

    public void unregisterReceivers() {
        if (null != downloadReceiver) {
            activity.unregisterReceiver(downloadReceiver);
            downloadReceiver = null;
        }
        if (null != installReceiver) {
            activity.unregisterReceiver(installReceiver);
            installReceiver = null;
        }
    }

    public void registerReceivers() {
        if (null == downloadReceiver) {
            downloadReceiver = new DetailsDownloadReceiver(
                activity,
                (Button) activity.findViewById(R.id.download)
            );
        }
        if (null == installReceiver) {
            installReceiver = new DetailsInstallReceiver(
                activity,
                (Button) activity.findViewById(R.id.download),
                (Button) activity.findViewById(R.id.uninstall)
            );
        }
    }

    public void downloadOrInstall() {
        File dir = apkPath.getParentFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }
        if (dir.exists() && dir.isDirectory() && dir.canWrite()) {
            if (apkPath.exists()) {
                install();
            } else {
                getPurchaseTask().execute();
            }
        } else {
            Toast.makeText(
                this.activity.getApplicationContext(),
                this.activity.getString(R.string.error_downloads_directory_not_writable),
                Toast.LENGTH_LONG
            ).show();
        }
    }

    public void install() {
        disableButton(R.id.download, R.string.details_installing);
        NotificationManager manager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(app.getDisplayName().hashCode());
        InstallerFactory.get(activity).install(app);
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

    private void disableButton(int buttonId, int stringId) {
        Button button = (Button) activity.findViewById(buttonId);
        button.setText(stringId);
        button.setEnabled(false);
    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            return activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            activity.requestPermissions(
                new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE },
                DetailsActivity.PERMISSIONS_REQUEST_CODE
            );
        }
    }
}
