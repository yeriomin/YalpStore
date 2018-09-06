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

import java.util.Random;

abstract public class NotificationBuilder {

    protected Context context;

    abstract public NotificationBuilder setTitle(String title);
    abstract public NotificationBuilder setMessage(String message);
    abstract public NotificationBuilder setIntent(Intent intent);
    abstract public Notification build();

    public NotificationBuilder addAction(int iconId, int stringId, PendingIntent pendingIntent) {
        return this;
    }

    public NotificationBuilder setProgress(int max, int progress) {
        return this;
    }

    public NotificationBuilder(Context context) {
        this.context = context.getApplicationContext();
    }

    protected PendingIntent getPendingIntent(Intent intent) {
        return isActivityIntent(intent) ? PendingIntent.getActivity(context, new Random().nextInt(), intent, 0) : PendingIntent.getBroadcast(context, new Random().nextInt(), intent, 0);
    }

    private boolean isActivityIntent(Intent intent) {
        return null != intent.getComponent() && intent.getComponent().getClassName().contains("Activity");
    }
}
