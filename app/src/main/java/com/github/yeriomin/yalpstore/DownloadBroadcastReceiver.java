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
    public void onReceive(Context c, Intent i) {
        Bundle extras = i.getExtras();
        DownloadManager.Query q = new DownloadManager.Query();
        q.setFilterById(extras.getLong(DownloadManager.EXTRA_DOWNLOAD_ID));

        DownloadManager dm = (DownloadManager) c.getSystemService(DOWNLOAD_SERVICE);
        Cursor cursor = dm.query(q);
        if (cursor.moveToFirst()) {
            int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
            int reason = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_REASON));
            String packageName = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TITLE));
            if (status == DownloadManager.STATUS_SUCCESSFUL || reason == DownloadManager.ERROR_FILE_ALREADY_EXISTS) {
                File file = new File(Uri.parse(cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))).getPath());
                Intent openApkIntent = PlayStoreApiWrapper.getOpenApkIntent(c, file);
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(c);
                if (sharedPreferences.getBoolean(PreferenceActivity.PREFERENCE_AUTO_INSTALL, false)) {
                    c.startActivity(openApkIntent);
                } else {
                    createNotification(c, openApkIntent, packageName, c.getString(R.string.notification_download_complete));
                    toast(c, c.getString(R.string.notification_download_complete_toast, packageName));
                }
            } else if (reason > 0) {
                String error = getErrorString(c, reason);
                toast(c, error);
                createNotification(c, new Intent(), packageName, error);
            }
        }
    }

    private void toast(Context c, String message) {
        Toast.makeText(c, message, Toast.LENGTH_LONG).show();
    }

    private String getErrorString(Context context, int reason) {
        int stringId;
        switch (reason) {
            case DownloadManager.ERROR_CANNOT_RESUME:
                stringId = R.string.download_manager_ERROR_CANNOT_RESUME;
                break;
            case DownloadManager.ERROR_DEVICE_NOT_FOUND:
                stringId = R.string.download_manager_ERROR_DEVICE_NOT_FOUND;
                break;
            case DownloadManager.ERROR_FILE_ERROR:
                stringId = R.string.download_manager_ERROR_FILE_ERROR;
                break;
            case DownloadManager.ERROR_HTTP_DATA_ERROR:
                stringId = R.string.download_manager_ERROR_HTTP_DATA_ERROR;
                break;
            case DownloadManager.ERROR_INSUFFICIENT_SPACE:
                stringId = R.string.download_manager_ERROR_INSUFFICIENT_SPACE;
                break;
            case DownloadManager.ERROR_TOO_MANY_REDIRECTS:
                stringId = R.string.download_manager_ERROR_TOO_MANY_REDIRECTS;
                break;
            case DownloadManager.ERROR_UNHANDLED_HTTP_CODE:
                stringId = R.string.download_manager_ERROR_UNHANDLED_HTTP_CODE;
                break;
            case 1010: // This constant hasn't been introduced in api 9
                stringId = R.string.download_manager_ERROR_BLOCKED;
                break;
            case DownloadManager.ERROR_UNKNOWN:
            default:
                stringId = R.string.download_manager_ERROR_UNKNOWN;
                break;
        }
        return context.getString(stringId);
    }

    private void createNotification(Context c, Intent intent, String packageName, String message) {
        PendingIntent pendingIntent = PendingIntent.getActivity(c, 1, intent, 0);
        Notification notification = NotificationUtil.createNotification(
            c,
            pendingIntent,
            packageName,
            message,
            R.mipmap.ic_launcher
        );
        NotificationManager manager = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(packageName.hashCode(), notification);
    }
}
