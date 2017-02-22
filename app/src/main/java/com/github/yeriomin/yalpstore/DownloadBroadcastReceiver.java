package com.github.yeriomin.yalpstore;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.github.yeriomin.yalpstore.model.App;

import java.io.File;

import static android.content.Context.DOWNLOAD_SERVICE;

public class DownloadBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context c, Intent i) {
        Bundle extras = i.getExtras();
        long downloadId = extras.getLong(DownloadManager.EXTRA_DOWNLOAD_ID);

        DownloadState state = DownloadState.get(downloadId);
        if (null == state) {
            return;
        }
        App app = state.getApp();

        try {
            int errorCode = getDownloadResult(c, downloadId);
            state.setFinished(downloadId);
            if (errorCode == 0) {
                state.setSuccessful(downloadId);
            } else {
                String error = getErrorString(c, errorCode);
                toast(c, error);
                new NotificationUtil(c).show(new Intent(), app.getDisplayName(), error);
            }
        } catch (Exception e) {
            // Download not finished
        }

        if (state.isEverythingFinished() && state.isEverythingSuccessful()) {
            verifyAndInstall(c, app);
        }
    }

    private int getDownloadResult(Context c, long downloadId) throws Exception {
        DownloadManager.Query q = new DownloadManager.Query();
        q.setFilterById(downloadId);

        DownloadManager dm = (DownloadManager) c.getSystemService(DOWNLOAD_SERVICE);
        Cursor cursor = dm.query(q);
        if (!cursor.moveToFirst()) {
            throw new Exception("Not finished");
        }

        int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
        int reason = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_REASON));
        if (status == DownloadManager.STATUS_SUCCESSFUL
            || reason == DownloadManager.ERROR_FILE_ALREADY_EXISTS
        ) {
            return 0;
        } else if (reason == 0) {
            return -1;
        } else {
            return reason;
        }
    }

    private void verifyAndInstall(Context c, App app) {
        File file = Downloader.getApkPath(app.getPackageName(), app.getVersionCode());
        Intent openApkIntent = DownloadOrInstallManager.getOpenApkIntent(c, file);
        if (new ApkSignatureVerifier(c).match(app.getPackageName(), file)) {
            if (shouldAutoInstall(c)) {
                c.startActivity(openApkIntent);
            } else {
                new NotificationUtil(c).show(openApkIntent, app.getDisplayName(), c.getString(R.string.notification_download_complete));
                toast(c, c.getString(R.string.notification_download_complete_toast, app.getDisplayName()));
            }
        } else {
            new NotificationUtil(c).show(openApkIntent, app.getDisplayName(), c.getString(R.string.notification_download_complete_signature_mismatch));
            toast(c, c.getString(R.string.notification_download_complete_signature_mismatch_toast, app.getDisplayName()));
        }
    }

    private boolean shouldAutoInstall(Context c) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(c);
        return sharedPreferences.getBoolean(PreferenceActivity.PREFERENCE_AUTO_INSTALL, false);
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
}
