package com.github.yeriomin.yalpstore;


import com.github.yeriomin.playstoreapi.AndroidAppDeliveryData;
import com.github.yeriomin.playstoreapi.BuyResponse;
import com.github.yeriomin.playstoreapi.DeliveryResponse;
import com.github.yeriomin.playstoreapi.GooglePlayAPI;
import com.github.yeriomin.yalpstore.model.App;

import java.io.IOException;

public class DeliveryDataTask extends GoogleApiAsyncTask {

    protected App app;
    protected AndroidAppDeliveryData deliveryData;

    public void setApp(App app) {
        this.app = app;
    }

    @Override
    protected Throwable doInBackground(String... params) {
        try {
            GooglePlayAPI api = new PlayStoreApiAuthenticator(context).getApi();
            BuyResponse buyResponse = api.purchase(app.getPackageName(), app.getVersionCode(), app.getOfferType());
            if (buyResponse.hasPurchaseStatusResponse()
                && buyResponse.getPurchaseStatusResponse().hasAppDeliveryData()
                && buyResponse.getPurchaseStatusResponse().getAppDeliveryData().hasDownloadUrl()
            ) {
                deliveryData = buyResponse.getPurchaseStatusResponse().getAppDeliveryData();
                return null;
            }
            if (!buyResponse.hasDownloadToken()) {
                return null;
            }
            DeliveryResponse deliveryResponse = api.delivery(
                app.getPackageName(),
                app.getInstalledVersionCode() >= app.getVersionCode() ? 0 : app.getInstalledVersionCode(),
                app.getVersionCode(),
                app.getOfferType(),
                GooglePlayAPI.PATCH_FORMAT.GZIPPED_GDIFF,
                buyResponse.getDownloadToken()
            );
            if (deliveryResponse.hasAppDeliveryData()
                && deliveryResponse.getAppDeliveryData().hasDownloadUrl()
            ) {
                deliveryData = deliveryResponse.getAppDeliveryData();
            }
        } catch (IOException e) {
            return e;
        }
        return null;
    }
}
