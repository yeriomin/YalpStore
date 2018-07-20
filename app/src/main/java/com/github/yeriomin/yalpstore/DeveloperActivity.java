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

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.github.yeriomin.playstoreapi.Base64;
import com.github.yeriomin.playstoreapi.DeveloperAppsRequest;
import com.github.yeriomin.playstoreapi.DeveloperIdContainer;
import com.github.yeriomin.playstoreapi.GooglePlayAPI;
import com.github.yeriomin.playstoreapi.UrlRequestWrapper;

import java.net.URLEncoder;

import static com.github.yeriomin.playstoreapi.Base64.NO_WRAP;

public class DeveloperActivity extends YalpStoreActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String developerId = getDeveloperId(getIntent());
        if (TextUtils.isEmpty(developerId)) {
            Log.i(getClass().getSimpleName(), "Developer id is empty");
            finish();
            return;
        }
        Log.i(getClass().getSimpleName(), "Developer id is " + developerId + ". Redirecting to the list activity");
        ClusterActivity.start(this, getClusterUrl(developerId), " ");
        finish();
    }

    private String getDeveloperId(Intent intent) {
        if (null == intent.getData() || TextUtils.isEmpty(intent.getData().getQueryParameter("id"))) {
            return null;
        }
        return intent.getData().getQueryParameter("id").trim();
    }

    private String getClusterUrl(String developerId) {
        return GooglePlayAPI.FDFE_URL + "cluster?ecp=" + URLEncoder.encode(Base64.encodeToString(getProtobufRequest(developerId), NO_WRAP));
    }

    /**
     * Unknown ints are magic. They probably define which list to return: all apps or featured...
     *
     */
    private byte[] getProtobufRequest(String developerId) {
        DeveloperIdContainer container = DeveloperIdContainer.newBuilder()
            .setDeveloperId(developerId)
            .setUnknownInt2(8)
            .setUnknownInt3(3)
            .build()
        ;
        return UrlRequestWrapper.newBuilder().setDeveloperAppsRequest(
            DeveloperAppsRequest.newBuilder()
                .setDeveloperIdContainer1(container)
                .setDeveloperIdContainer2(container)
                .setUnknownInt3(0)
        ).build().toByteArray();
    }
}
