package com.github.yeriomin.yalpstore;

import com.github.yeriomin.yalpstore.notification.NotificationManagerWrapper;

class BackgroundPurchaseTask extends PurchaseTask {

    @Override
    protected void onPostExecute(Throwable e) {
        super.onPostExecute(e);
        if (null != e) {
            new NotificationManagerWrapper(context).show(
                DetailsActivity.getDetailsIntent(context, app.getPackageName()),
                app.getDisplayName(),
                context.getString(R.string.error_could_not_download)
            );
        }
    }
}
