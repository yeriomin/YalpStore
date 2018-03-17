package in.dragons.galaxy.task.playstore;

import android.util.Log;

import com.github.yeriomin.playstoreapi.AndroidAppDeliveryData;
import com.github.yeriomin.playstoreapi.BuyResponse;
import com.github.yeriomin.playstoreapi.DeliveryResponse;
import com.github.yeriomin.playstoreapi.GooglePlayAPI;

import java.io.IOException;

import in.dragons.galaxy.NotPurchasedException;
import in.dragons.galaxy.fragment.PreferenceFragment;
import in.dragons.galaxy.R;
import in.dragons.galaxy.model.App;

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

    protected String getRestrictionString() {
        switch (app.getRestriction()) {
            case GooglePlayAPI.AVAILABILITY_NOT_RESTRICTED:
                return null;
            case GooglePlayAPI.AVAILABILITY_RESTRICTED_GEO:
                return context.getString(R.string.availability_restriction_country);
            case GooglePlayAPI.AVAILABILITY_INCOMPATIBLE_DEVICE_APP:
                return context.getString(R.string.availability_restriction_hardware_app);
            default:
                return context.getString(R.string.availability_restriction_generic);
        }
    }

    private boolean shouldDownloadDelta() {
        return PreferenceFragment.getBoolean(context, PreferenceFragment.PREFERENCE_DOWNLOAD_DELTAS)
                && app.getInstalledVersionCode() < app.getVersionCode()
                ;
    }
}
