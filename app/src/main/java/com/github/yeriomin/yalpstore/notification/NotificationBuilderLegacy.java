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

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.github.yeriomin.yalpstore.R;

import java.lang.reflect.Method;

class NotificationBuilderLegacy extends NotificationBuilder {

    private boolean ongoing;
    private String title;
    private String message;
    private Intent intent;

    @Override
    public NotificationBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    @Override
    public NotificationBuilder setMessage(String message) {
        this.message = message;
        return this;
    }

    @Override
    public NotificationBuilder setIntent(Intent intent) {
        this.intent = intent;
        return this;
    }

    @Override
    public Notification build() {
        Notification notification = new Notification();
        notification.icon = ongoing ? R.drawable.ic_download_animation : R.drawable.ic_notification;
        try {
            Method m = notification.getClass().getMethod("setLatestEventInfo", Context.class, CharSequence.class, CharSequence.class, PendingIntent.class);
            m.invoke(notification, context, title, message, getPendingIntent(intent));
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
            if (ongoing) {
                notification.flags |= Notification.FLAG_ONGOING_EVENT;
                notification.flags |= Notification.FLAG_ONLY_ALERT_ONCE;
            }
        } catch (Exception e) {
            // do nothing
        }
        return notification;
    }

    @Override
    public NotificationBuilder setProgress(int max, int progress) {
        ongoing = true;
        return super.setProgress(max, progress);
    }

    public NotificationBuilderLegacy(Context context) {
        super(context);
    }
}
