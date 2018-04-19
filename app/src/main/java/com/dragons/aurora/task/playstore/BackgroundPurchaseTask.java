package com.dragons.aurora.task.playstore;

import com.dragons.aurora.playstoreapiv2.AndroidAppDeliveryData;

import com.dragons.aurora.activities.DetailsActivity;
import com.dragons.aurora.R;
import com.dragons.aurora.notification.NotificationManagerWrapper;

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
