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

import com.github.yeriomin.playstoreapi.DocV2;
import com.github.yeriomin.playstoreapi.GooglePlayAPI;
import com.github.yeriomin.yalpstore.LocalWishlist;
import com.github.yeriomin.yalpstore.PlayStoreApiAuthenticator;
import com.github.yeriomin.yalpstore.PreferenceUtil;
import com.github.yeriomin.yalpstore.model.App;
import com.github.yeriomin.yalpstore.model.AppBuilder;
import com.github.yeriomin.yalpstore.task.InstalledAppsTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class WishlistUpdateTask extends PlayStorePayloadTask<List<String>> implements CloneableTask {

    protected List<App> apps = new ArrayList<>();

    @Override
    public CloneableTask clone() {
        WishlistUpdateTask task = new WishlistUpdateTask();
        task.setErrorView(errorView);
        task.setProgressIndicator(progressIndicator);
        task.setContext(context);
        return task;
    }

    @Override
    protected List<String> getResult(GooglePlayAPI api, String... arguments) throws IOException {
        List<String> packageNames = new ArrayList<>();
        InstalledAppsTask installedAppsTask = new InstalledAppsTask();
        installedAppsTask.setContext(context);
        installedAppsTask.setIncludeSystemApps(true);
        Set<String> installedPackageNames = installedAppsTask.getInstalledApps(false).keySet();
        if (PreferenceUtil.getBoolean(context, PlayStoreApiAuthenticator.PREFERENCE_APP_PROVIDED_EMAIL)) {
            packageNames.addAll(Arrays.asList(new LocalWishlist(context).get()));
        } else {
            for (DocV2 doc: api.getWishlistApps().getDoc(0).getChild(0).getChildList()) {
                App app = AppBuilder.build(doc);
                if (installedPackageNames.contains(app.getPackageName())) {
                    continue;
                }
                apps.add(app);
                packageNames.add(app.getPackageName());
            }
        }
        packageNames.removeAll(installedPackageNames);
        return packageNames;
    }

    @Override
    protected void onPostExecute(List<String> packageNames) {
        super.onPostExecute(packageNames);
        if (success() && !PreferenceUtil.getBoolean(context, PlayStoreApiAuthenticator.PREFERENCE_APP_PROVIDED_EMAIL)) {
            new LocalWishlist(context).update(packageNames);
        }
    }
}
