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

package com.github.yeriomin.yalpstore.task.playstore;

import android.content.pm.PackageManager;

import com.github.yeriomin.playstoreapi.DetailsResponse;
import com.github.yeriomin.playstoreapi.GooglePlayAPI;
import com.github.yeriomin.playstoreapi.GooglePlayException;
import com.github.yeriomin.yalpstore.BuildConfig;
import com.github.yeriomin.yalpstore.ContextUtil;
import com.github.yeriomin.yalpstore.R;
import com.github.yeriomin.yalpstore.YalpStoreApplication;
import com.github.yeriomin.yalpstore.model.App;
import com.github.yeriomin.yalpstore.model.AppBuilder;
import com.github.yeriomin.yalpstore.selfupdate.UpdaterFactory;

import java.io.IOException;

public class DetailsTask extends PlayStorePayloadTask<App> {

    protected String packageName;

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    @Override
    protected void processIOException(IOException e) {
        if (null != e
            && e instanceof GooglePlayException
            && ((GooglePlayException) e).getCode() == 404
            && YalpStoreApplication.isForeground()
        ) {
            ContextUtil.toast(this.context, R.string.details_not_available_on_play_store);
        }
    }

    @Override
    protected App getResult(GooglePlayAPI api, String... arguments) throws IOException {
        DetailsResponse response = api.details(packageName);
        App app = AppBuilder.build(response);
        PackageManager pm = context.getPackageManager();
        try {
            app.getPackageInfo().applicationInfo = pm.getApplicationInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            // App is not installed
        }
        if (YalpStoreApplication.installedPackages.containsKey(packageName)) {
            app.setPackageInfo(YalpStoreApplication.installedPackages.get(packageName).getPackageInfo());
        }
        return app;
    }

    @Override
    protected App doInBackground(String... arguments) {
        return packageName.equals(BuildConfig.APPLICATION_ID) ? getSelf() : super.doInBackground(arguments);
    }

    private App getSelf() {
        App app = YalpStoreApplication.installedPackages.get(BuildConfig.APPLICATION_ID);
        int latestVersionCode = UpdaterFactory.get(context).getLatestVersionCode();
        app.setVersionCode(latestVersionCode);
        app.setVersionName("0." + latestVersionCode);
        return app;
    }
}
