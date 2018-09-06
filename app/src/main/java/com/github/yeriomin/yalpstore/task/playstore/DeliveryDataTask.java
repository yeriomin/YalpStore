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

import android.util.Log;

import com.github.yeriomin.playstoreapi.AndroidAppDeliveryData;
import com.github.yeriomin.playstoreapi.BuyResponse;
import com.github.yeriomin.playstoreapi.DeliveryResponse;
import com.github.yeriomin.playstoreapi.GooglePlayAPI;
import com.github.yeriomin.yalpstore.NotPurchasedException;
import com.github.yeriomin.yalpstore.PreferenceUtil;
import com.github.yeriomin.yalpstore.YalpStoreApplication;
import com.github.yeriomin.yalpstore.model.App;

import java.io.IOException;

public class DeliveryDataTask extends PlayStorePayloadTask<AndroidAppDeliveryData> {

    protected App app;
    protected String downloadToken;
    protected AndroidAppDeliveryData deliveryData;

    public void setApp(App app) {
        this.app = app;
    }

    @Override
    protected AndroidAppDeliveryData getResult(GooglePlayAPI api, String... arguments) throws IOException {
        purchase(api);
        delivery(api);
        return deliveryData;
    }

    protected void purchase(GooglePlayAPI api) {
        try {
            BuyResponse buyResponse = api.purchase(app.getPackageName(), app.getVersionCode(), app.getOfferType());
            if (buyResponse.hasPurchaseStatusResponse()
                && buyResponse.getPurchaseStatusResponse().hasAppDeliveryData()
                && buyResponse.getPurchaseStatusResponse().getAppDeliveryData().hasDownloadUrl()
            ) {
                deliveryData = buyResponse.getPurchaseStatusResponse().getAppDeliveryData();
            }
            if (buyResponse.hasDownloadToken()) {
                downloadToken = buyResponse.getDownloadToken();
            }
        } catch (IOException e) {
            Log.w(getClass().getSimpleName(), "Purchase for " + app.getPackageName() + " failed with " + e.getClass().getName() + ": " + e.getMessage());
        }
    }

    protected void delivery(GooglePlayAPI api) throws IOException {
        DeliveryResponse deliveryResponse = api.delivery(
            app.getPackageName(),
            shouldDownloadDelta() ? app.getInstalledVersionCode() : 0,
            app.getVersionCode(),
            app.getOfferType(),
            downloadToken
        );
        if (deliveryResponse.hasAppDeliveryData()
            && deliveryResponse.getAppDeliveryData().hasDownloadUrl()
        ) {
            deliveryData = deliveryResponse.getAppDeliveryData();
        } else if (!app.isFree() && !YalpStoreApplication.user.appProvidedEmail()) {
            throw new NotPurchasedException();
        }
    }

    private boolean shouldDownloadDelta() {
        return PreferenceUtil.getBoolean(context, PreferenceUtil.PREFERENCE_DOWNLOAD_DELTAS)
            && app.getInstalledVersionCode() < app.getVersionCode()
        ;
    }
}
