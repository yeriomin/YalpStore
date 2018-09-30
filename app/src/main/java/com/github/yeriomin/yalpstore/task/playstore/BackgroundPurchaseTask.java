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
                app.getPackageName(),
                DetailsActivity.getDetailsIntent(context, app.getPackageName()),
                app.getDisplayName(),
                context.getString(R.string.error_could_not_download)
            );
        }
    }
}
