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

package com.github.yeriomin.yalpstore.notification;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.github.yeriomin.yalpstore.DetailsActivity;
import com.github.yeriomin.yalpstore.DownloadState;
import com.github.yeriomin.yalpstore.InstallerDefault;
import com.github.yeriomin.yalpstore.PackageSpecificReceiver;
import com.github.yeriomin.yalpstore.task.InstallTask;

import java.io.File;

public class DownloadChecksumReceiver extends PackageSpecificReceiver {

    static public final String ACTION_CHECK_APK = "ACTION_CHECK_APK";

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (TextUtils.isEmpty(packageName)) {
            Log.w(getClass().getSimpleName(), "No package name provided in the intent");
            return;
        }
        DownloadState downloadState = DownloadState.get(packageName);
        if (null == downloadState || null == downloadState.getApkChecksum()) {
            Log.w(getClass().getSimpleName(), "No download checksum found for " + packageName);
            deleteApk(context, packageName);
            context.startActivity(DetailsActivity.getDetailsIntent(context, packageName));
            return;
        }
        Log.i(getClass().getSimpleName(), "Launching installer for " + packageName);
        InstallerDefault installerDefault = new InstallerDefault(context);
        installerDefault.setBackground(false);
        new InstallTask(installerDefault, downloadState.getApp()).execute();
    }

    private void deleteApk(Context context, String packageName) {
        for (File file: context.getFilesDir().listFiles()) {
            if (file.getAbsolutePath().contains(packageName) && file.getAbsolutePath().endsWith(".apk")) {
                file.delete();
                return;
            }
        }
    }
}
