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

package com.github.yeriomin.yalpstore.notification;

import android.annotation.TargetApi;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.github.yeriomin.yalpstore.R;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
class NotificationBuilderHoneycomb extends NotificationBuilder {

    protected Notification.Builder builder;

    @Override
    public NotificationBuilder setTitle(String title) {
        builder.setContentTitle(title);
        return this;
    }

    @Override
    public NotificationBuilder setMessage(String message) {
        builder.setContentText(message);
        return this;
    }

    @Override
    public NotificationBuilder setIntent(Intent intent) {
        builder.setContentIntent(getPendingIntent(intent));
        return this;
    }

    @Override
    public Notification build() {
        return builder.getNotification();
    }

    public NotificationBuilderHoneycomb(Context context) {
        super(context);
        builder = new Notification.Builder(context)
            .setSmallIcon(R.drawable.ic_notification)
            .setAutoCancel(true)
        ;
    }

    @Override
    public NotificationBuilder setProgress(int max, int progress) {
        builder
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setSmallIcon(R.drawable.ic_download_animation)
        ;
        return super.setProgress(max, progress);
    }
}
