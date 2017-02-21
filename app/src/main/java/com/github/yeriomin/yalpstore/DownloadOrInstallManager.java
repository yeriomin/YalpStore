package com.github.yeriomin.yalpstore;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.yeriomin.yalpstore.model.App;

import java.io.File;

public class DownloadOrInstallManager extends DetailsManager {

    private File apkPath;
    private DetailsDownloadReceiver receiver;

    public DownloadOrInstallManager(DetailsActivity activity, App app) {
        super(activity, app);
    }

    @Override
    public void draw() {
        Button downloadButton = (Button) activity.findViewById(R.id.download);
        if (app.getVersionCode() == 0) {
            downloadButton.setText(activity.getString(R.string.details_download_impossible));
            downloadButton.setEnabled(false);
        } else {
            apkPath = Downloader.getApkPath(app.getPackageName(), app.getVersionCode());
            if (apkPath.exists()) {
                downloadButton.setText(R.string.details_install);
            }
            downloadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkPermission()) {
                        downloadOrInstall();
                    } else {
                        requestPermission();
                    }
                }
            });
        }
    }

    public void unregisterReceiver() {
        activity.unregisterReceiver(receiver);
    }

    public void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        receiver = new DetailsDownloadReceiver();
        receiver.setButton((Button) activity.findViewById(R.id.download));
        activity.registerReceiver(receiver, filter);
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
        Log.i(this.getClass().getName(), apkPath.getName() + " exists. No download needed.");
        NotificationManager manager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(app.getDisplayName().hashCode());
        ApkSignatureVerifier verifier = new ApkSignatureVerifier(activity);
        if (verifier.match(app.getPackageName(), apkPath)) {
            activity.startActivity(PlayStoreApiWrapper.getOpenApkIntent(activity, apkPath));
        } else {
            getSignatureMismatchDialog().show();
        }
    }

    private AlertDialog getSignatureMismatchDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder
            .setMessage(R.string.details_signature_mismatch)
            .setPositiveButton(
                android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                }
            )
        ;
        return builder.create();
    }

    private PurchaseTask getPurchaseTask() {
        PurchaseTask purchaseTask = new PurchaseTask() {
            @Override
            protected void onPostExecute(Throwable e) {
                super.onPostExecute(e);
                if (null == e) {
                    Button button = (Button) activity.findViewById(R.id.download);
                    button.setText(R.string.details_downloading);
                    button.setEnabled(false);
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
