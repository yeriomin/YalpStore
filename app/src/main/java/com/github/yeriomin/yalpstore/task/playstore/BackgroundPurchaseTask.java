package com.github.yeriomin.yalpstore.task.playstore;

import com.github.yeriomin.playstoreapi.AndroidAppDeliveryData;
import com.github.yeriomin.yalpstore.DetailsActivity;
import com.github.yeriomin.yalpstore.R;
import com.github.yeriomin.yalpstore.notification.NotificationManagerWrapper;

class BackgroundPurchaseTask extends PurchaseTask {

    @Override
    protected void onPostExecute(AndroidAppDeliveryData deliveryData) {
        super.onPostExecute(deliveryData);
        if (!success()) {
            new NotificationManagerWrapper(context).show(
                DetailsActivity.getDetailsIntent(context, app.getPackageName()),
                app.getDisplayName(),
                context.getString(R.string.error_could_not_download)
            );
        }
    }
}
