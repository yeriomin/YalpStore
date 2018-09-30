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

import com.github.yeriomin.yalpstore.BlackWhiteListManager;
import com.github.yeriomin.yalpstore.PackageSpecificReceiver;
import com.github.yeriomin.yalpstore.Paths;

public class IgnoreUpdatesReceiver extends PackageSpecificReceiver {

    static public final String ACTION_IGNORE_UPDATES = "ACTION_IGNORE_UPDATES";

    static public final String VERSION_CODE = "VERSION_CODE";

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (TextUtils.isEmpty(packageName)) {
            Log.w(getClass().getSimpleName(), "No package name provided in the intent");
            return;
        }
        Log.i(getClass().getSimpleName(), "Adding " + packageName + " to ignore list");
        BlackWhiteListManager manager = new BlackWhiteListManager(context);
        if (manager.isBlack()) {
            manager.add(packageName);
        } else {
            manager.remove(packageName);
        }
        new NotificationManagerWrapper(context).cancel(packageName);
        Paths.getApkPath(context, packageName, intent.getIntExtra(VERSION_CODE, 0)).delete();
    }
}
