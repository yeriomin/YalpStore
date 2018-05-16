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

import android.content.Context;
import android.util.Log;

import com.github.yeriomin.playstoreapi.AndroidAppDeliveryData;
import com.github.yeriomin.playstoreapi.GooglePlayAPI;
import com.github.yeriomin.yalpstore.model.App;

import java.io.File;

public class DownloadRequestBuilderDelta extends DownloadRequestBuilderApk {

    public DownloadRequestBuilderDelta(Context context, App app, AndroidAppDeliveryData deliveryData) {
        super(context, app, deliveryData);
    }

    @Override
    protected String getDownloadUrl() {
        DownloadState.get(app.getPackageName()).setPatchFormat(getPatchFormat(deliveryData.getPatchData().getPatchFormat()));
        return deliveryData.getPatchData().getDownloadUrl();
    }

    @Override
    protected File getDestinationFile() {
        return Paths.getDeltaPath(context, app.getPackageName(), app.getVersionCode());
    }

    private GooglePlayAPI.PATCH_FORMAT getPatchFormat(int patchFromat) {
        switch (patchFromat) {
            case 1:
                return GooglePlayAPI.PATCH_FORMAT.GDIFF;
            case 2:
                return GooglePlayAPI.PATCH_FORMAT.GZIPPED_GDIFF;
            case 3:
                return GooglePlayAPI.PATCH_FORMAT.GZIPPED_BSDIFF;
        }
        return null;
    }
}
