package com.github.yeriomin.yalpstore.notification;

import android.app.IntentService;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.github.yeriomin.yalpstore.DetailsActivity;
import com.github.yeriomin.yalpstore.DownloadState;
import com.github.yeriomin.yalpstore.InstallerDefault;

import java.io.File;

public class DownloadChecksumService extends IntentService {

    static public final String PACKAGE_NAME = "PACKAGE_NAME";

    public DownloadChecksumService() {
        super("DownloadChecksumService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String packageName = intent.getStringExtra(PACKAGE_NAME);
        if (TextUtils.isEmpty(packageName)) {
            Log.w(getClass().getSimpleName(), "No package name provided in the intent");
            return;
        }
        DownloadState downloadState = DownloadState.get(packageName);
        if (null == downloadState || null == downloadState.getApkChecksum()) {
            Log.w(getClass().getSimpleName(), "No download checksum found for " + packageName);
            deleteApk(packageName);
            getApplicationContext().startActivity(DetailsActivity.getDetailsIntent(getApplicationContext(), packageName));
            return;
        }
        Log.i(getClass().getSimpleName(), "Launching installer for " + packageName);
        InstallerDefault installerDefault = new InstallerDefault(getApplicationContext());
        installerDefault.setBackground(false);
        installerDefault.verifyAndInstall(downloadState.getApp());
    }

    private void deleteApk(String packageName) {
        for (File file: getFilesDir().listFiles()) {
            if (file.getAbsolutePath().contains(packageName) && file.getAbsolutePath().endsWith(".apk")) {
                file.delete();
                return;
            }
        }
    }
}
