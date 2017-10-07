package com.github.yeriomin.yalpstore.task.playstore;

import android.util.Log;

import com.github.yeriomin.playstoreapi.AndroidAppDeliveryData;
import com.github.yeriomin.playstoreapi.BuyResponse;
import com.github.yeriomin.playstoreapi.DeliveryResponse;
import com.github.yeriomin.playstoreapi.GooglePlayAPI;
import com.github.yeriomin.yalpstore.NotPurchasedException;
import com.github.yeriomin.yalpstore.PreferenceActivity;
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
            Log.w(getClass().getName(), "Purchase for " + app.getPackageName() + " failed with " + e.getClass().getName() + ": " + e.getMessage());
        }
    }

    protected void delivery(GooglePlayAPI api) throws IOException {
        DeliveryResponse deliveryResponse = api.delivery(
            app.getPackageName(),
            shouldDownloadDelta() ? app.getInstalledVersionCode() : 0,
            app.getVersionCode(),
            app.getOfferType(),
            GooglePlayAPI.PATCH_FORMAT.GZIPPED_GDIFF,
            downloadToken
        );
        if (deliveryResponse.hasAppDeliveryData()
            && deliveryResponse.getAppDeliveryData().hasDownloadUrl()
        ) {
            deliveryData = deliveryResponse.getAppDeliveryData();
        } else {
            throw new NotPurchasedException();
        }
    }

    private boolean shouldDownloadDelta() {
        return PreferenceActivity.getBoolean(context, PreferenceActivity.PREFERENCE_DOWNLOAD_DELTAS)
            && app.getInstalledVersionCode() < app.getVersionCode()
        ;
    }
}
