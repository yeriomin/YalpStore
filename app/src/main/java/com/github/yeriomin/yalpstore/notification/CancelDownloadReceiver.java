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

import com.github.yeriomin.yalpstore.DownloadManagerFactory;
import com.github.yeriomin.yalpstore.DownloadManagerInterface;
import com.github.yeriomin.yalpstore.DownloadState;
import com.github.yeriomin.yalpstore.PackageSpecificReceiver;
import com.github.yeriomin.yalpstore.Paths;
import com.github.yeriomin.yalpstore.YalpStoreApplication;

import java.util.ArrayList;
import java.util.List;

public class CancelDownloadReceiver extends PackageSpecificReceiver {

    static public final String ACTION_CANCEL_DOWNLOAD = "ACTION_CANCEL_DOWNLOAD";

    static public final String DOWNLOAD_ID = "DOWNLOAD_ID";

    private DownloadManagerInterface dm;

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        dm = DownloadManagerFactory.get(context);
        long downloadId = intent.getLongExtra(DOWNLOAD_ID, 0L);
        if (downloadId == 0 && TextUtils.isEmpty(packageName)) {
            Log.w(getClass().getSimpleName(), "No download id or package name provided in the intent");
        }
        List<Long> downloadIds = new ArrayList<>();
        if (downloadId != 0) {
            downloadIds.add(downloadId);
            if (TextUtils.isEmpty(packageName)) {
                DownloadState state = DownloadState.get(downloadId);
                if (null != state && null != state.getApp()) {
                    packageName = state.getApp().getPackageName();
                }
            }
        }
        if (TextUtils.isEmpty(packageName)) {
            return;
        }
        ((YalpStoreApplication) context.getApplicationContext()).removePendingUpdate(packageName);
        DownloadState state = DownloadState.get(packageName);
        state.setCancelled();
        downloadIds.addAll(state.getDownloadIds());
        for (long id: downloadIds) {
            cancel(id);
        }
        if (null != state.getApp()) {
            Paths.getApkPath(context, packageName, state.getApp().getVersionCode()).delete();
        }
    }

    private void cancel(long downloadId) {
        Log.i(getClass().getSimpleName(), "Cancelling download " + downloadId);
        dm.cancel(downloadId);
    }
}
