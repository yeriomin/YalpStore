package com.github.yeriomin.yalpstore;

import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import java.io.File;

import static android.content.Context.DOWNLOAD_SERVICE;

public class DownloadBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        DownloadManager.Query q = new DownloadManager.Query();
        q.setFilterById(extras.getLong(DownloadManager.EXTRA_DOWNLOAD_ID));

        DownloadManager dm = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
        Cursor c = dm.query(q);
        if (c.moveToFirst()) {
            int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
            int reason = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_REASON));
            if (status == DownloadManager.STATUS_SUCCESSFUL || reason == DownloadManager.ERROR_FILE_ALREADY_EXISTS) {
                File file = new File(Uri.parse(c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))).getPath());
                Intent i = PlayStoreApiWrapper.getOpenApkIntent(context, file);
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                if (sharedPreferences.getBoolean(PreferenceActivity.PREFERENCE_AUTO_INSTALL, false)) {
                    context.startActivity(i);
                } else {
                    String title = c.getString(c.getColumnIndex(DownloadManager.COLUMN_TITLE));
                    createNotification(context, i, title);
                    Toast.makeText(
                        context,
                        context.getString(R.string.notification_download_complete_toast, title),
                        Toast.LENGTH_LONG
                    ).show();
                }
            }
        }
    }

    private void createNotification(Context context, Intent intent, String packageName) {
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, intent, 0);
        Notification notification = NotificationUtil.createNotification(
            context,
            pendingIntent,
            packageName,
            context.getString(R.string.notification_download_complete),
            R.mipmap.ic_launcher
        );
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(packageName.hashCode(), notification);
    }
}
