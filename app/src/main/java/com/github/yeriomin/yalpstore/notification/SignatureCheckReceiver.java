package com.github.yeriomin.yalpstore.notification;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.github.yeriomin.yalpstore.InstallerDefault;
import com.github.yeriomin.yalpstore.PackageSpecificReceiver;
import com.github.yeriomin.yalpstore.download.DownloadManager;
import com.github.yeriomin.yalpstore.task.InstallTask;

public class SignatureCheckReceiver extends PackageSpecificReceiver {

    static public final String ACTION_CHECK_APK = "ACTION_CHECK_APK";

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (TextUtils.isEmpty(packageName)) {
            return;
        }
        Log.i(getClass().getSimpleName(), "Launching installer for " + packageName);
        InstallerDefault installerDefault = new InstallerDefault(context);
        installerDefault.setBackground(false);
        new InstallTask(installerDefault, DownloadManager.getApp(packageName)).execute();
    }
}
