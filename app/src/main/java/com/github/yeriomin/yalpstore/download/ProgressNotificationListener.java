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

package com.github.yeriomin.yalpstore.download;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.format.Formatter;

import com.github.yeriomin.yalpstore.DetailsActivity;
import com.github.yeriomin.yalpstore.R;
import com.github.yeriomin.yalpstore.notification.CancelDownloadReceiver;
import com.github.yeriomin.yalpstore.notification.NotificationBuilder;
import com.github.yeriomin.yalpstore.notification.NotificationManagerWrapper;

import java.lang.ref.WeakReference;

class ProgressNotificationListener implements DownloadManager.ProgressListener {

    private WeakReference<Context> contextRef;
    private String packageName;
    private String title;

    private NotificationBuilder notificationBuilder;

    public ProgressNotificationListener(Context context, String packageName, String title) {
        this.contextRef = new WeakReference<>(context);
        this.packageName = packageName;
        this.title = title;
    }

    @Override
    public void onProgress(long bytesDownloaded, long bytesTotal) {
        NotificationBuilder notificationBuilder = getNotificationBuilder();
        notificationBuilder
            .setMessage(contextRef.get().getString(
                R.string.notification_download_progress,
                Formatter.formatShortFileSize(contextRef.get(), bytesDownloaded),
                Formatter.formatShortFileSize(contextRef.get(), bytesTotal)
            ))
            .setProgress((int) bytesTotal, (int) bytesDownloaded)
        ;
        new NotificationManagerWrapper(contextRef.get()).show(
            packageName,
            notificationBuilder.build()
        );
    }

    @Override
    public void onCompletion() {
        new NotificationManagerWrapper(contextRef.get()).cancel(packageName);
    }

    private NotificationBuilder getNotificationBuilder() {
        if (null == notificationBuilder) {
            notificationBuilder = NotificationManagerWrapper.getBuilder(contextRef.get())
                .setTitle(title)
                .setIntent(DetailsActivity.getDetailsIntent(contextRef.get(), packageName))
                .addAction(R.drawable.ic_cancel, android.R.string.cancel, getCancelIntent())
            ;
        }
        return notificationBuilder;
    }

    private PendingIntent getCancelIntent() {
        Intent intentCancel = new Intent();
        intentCancel.setAction(CancelDownloadReceiver.ACTION_CANCEL_DOWNLOAD);
        intentCancel.putExtra(Intent.EXTRA_PACKAGE_NAME, packageName);
        return PendingIntent.getBroadcast(contextRef.get(), packageName.hashCode(), intentCancel, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
