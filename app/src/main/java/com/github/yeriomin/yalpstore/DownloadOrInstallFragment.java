package com.github.yeriomin.yalpstore;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.github.yeriomin.yalpstore.model.App;

import java.io.File;

public class DownloadOrInstallFragment extends DetailsFragment {

    private File apkPath;
    private DetailsDownloadReceiver downloadReceiver;
    private DetailsInstallReceiver installReceiver;

    static public Intent getOpenApkIntent(Context context, File file) {
        Intent intent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
            intent.setData(FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileprovider", file));
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        return intent;
    }

    public DownloadOrInstallFragment(DetailsActivity activity, App app) {
        super(activity, app);
    }

    @Override
    public void draw() {
        apkPath = Downloader.getApkPath(app.getPackageName(), app.getVersionCode());
        drawUninstallButton();
        drawDownloadButton();
        drawMoreButton();
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

    private void drawMoreButton() {
        ImageButton more = (ImageButton) activity.findViewById(R.id.more);
        if (null != more) {
            more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.startActivity(new Intent(activity, ManualDownloadActivity.class));
                }
            });
        }
    }

    public void unregisterReceivers() {
        activity.unregisterReceiver(downloadReceiver);
        downloadReceiver = null;
        activity.unregisterReceiver(installReceiver);
        installReceiver = null;
    }

    public void registerReceivers() {
        if (null == downloadReceiver) {
            registerDownloadReceiver();
        }
        if (null == installReceiver) {
            registerInstallReceiver();
        }
    }

    private void registerDownloadReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(DownloadManagerInterface.ACTION_DOWNLOAD_COMPLETE);
        downloadReceiver = new DetailsDownloadReceiver();
        downloadReceiver.setButton((Button) activity.findViewById(R.id.download));
        activity.registerReceiver(downloadReceiver, filter);
    }

    private void registerInstallReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_PACKAGE_INSTALL);
        filter.addAction(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        filter.addAction(DetailsInstallReceiver.ACTION_PACKAGE_REPLACED_NON_SYSTEM);
        installReceiver = new DetailsInstallReceiver();
        installReceiver.setButtonInstall((Button) activity.findViewById(R.id.download));
        installReceiver.setButtonUninstall((Button) activity.findViewById(R.id.uninstall));
        activity.registerReceiver(installReceiver, filter);
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
