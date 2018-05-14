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

import com.github.yeriomin.playstoreapi.GooglePlayAPI;
import com.github.yeriomin.playstoreapi.UrlIterator;
import com.github.yeriomin.yalpstore.AppListIterator;
import com.github.yeriomin.yalpstore.PreferenceUtil;
import com.github.yeriomin.yalpstore.YalpStoreApplication;
import com.github.yeriomin.yalpstore.model.App;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.github.yeriomin.yalpstore.PlayStoreApiAuthenticator.PREFERENCE_APP_PROVIDED_EMAIL;

public class PurchasedAppsTask extends PlayStorePayloadTask<List<App>> {

    @Override
    protected List<App> getResult(GooglePlayAPI api, String... arguments) throws IOException {
        List<App> apps = new ArrayList<>();
        if (!YalpStoreApplication.purchasedPackageNames.timeToUpdate()) {
            return apps;
        }
        YalpStoreApplication.purchasedPackageNames.clear();
        if (PreferenceUtil.getBoolean(context, PREFERENCE_APP_PROVIDED_EMAIL)) {
            return apps;
        }
        AppListIterator iterator = getIterator(api);
        while (iterator.hasNext()) {
            apps.addAll(iterator.next());
        }
        Set<String> packageNames = new HashSet<>();
        for (App app: apps) {
            if (!app.isFree()) {
                packageNames.add(app.getPackageName());
            }
        }
        YalpStoreApplication.purchasedPackageNames.clear();
        YalpStoreApplication.purchasedPackageNames.addAll(packageNames);
        return apps;
    }

    private AppListIterator getIterator(GooglePlayAPI api) {
        Map<String, String> params = new HashMap<>();
        params.put("n", "15");
        params.put("o", "0");
        params.put("esp", "EAriAQIKAA==");
        return new AppListIterator(new UrlIterator(
            api,
            api.getClient().buildUrl(GooglePlayAPI.FDFE_URL + "stream", params))
        );
    }
}
