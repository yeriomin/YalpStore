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

package com.github.yeriomin.yalpstore;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.text.TextUtils;

import java.lang.ref.WeakReference;

import static com.github.yeriomin.yalpstore.GlobalInstallReceiver.ACTION_INSTALL_UI_UPDATE;

public class DetailsInstallReceiver extends BroadcastReceiver {

    private WeakReference<DetailsActivity> activityRef;
    private String packageName;

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public DetailsInstallReceiver(DetailsActivity activity, String packageName) {
        activityRef = new WeakReference<>(activity);
        this.packageName = packageName;
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_INSTALL_UI_UPDATE);
        activity.registerReceiver(this, filter);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (TextUtils.isEmpty(intent.getStringExtra(Intent.EXTRA_PACKAGE_NAME))
            || !packageName.equals(intent.getStringExtra(Intent.EXTRA_PACKAGE_NAME))
            || null == DetailsActivity.app
        ) {
            return;
        }
        if (YalpStoreApplication.installedPackages.containsKey(packageName)) {
            DetailsActivity.app.getPackageInfo().versionCode = YalpStoreApplication.installedPackages.get(packageName).getVersionCode();
            DetailsActivity.app.setInstalled(true);
        } else {
            DetailsActivity.app.getPackageInfo().versionCode = 0;
            DetailsActivity.app.setInstalled(false);
        }
        DetailsActivity activity = activityRef.get();
        if (null == activity || !ContextUtil.isAlive(activity)) {
            return;
        }
        activity.redrawDetails(DetailsActivity.app);
    }
}
