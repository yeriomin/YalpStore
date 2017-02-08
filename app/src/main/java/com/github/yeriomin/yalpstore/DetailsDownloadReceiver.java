package com.github.yeriomin.yalpstore;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;

public class DetailsDownloadReceiver extends BroadcastReceiver {

    private long downloadId;
    private Button button;

    public void setDownloadId(long downloadId) {
        this.downloadId = downloadId;
    }

    public void setButton(Button button) {
        this.button = button;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (downloadId == 0) {
            return;
        }
        Bundle extras = intent.getExtras();
        if (null == extras) {
            return;
        }
        long id = extras.getLong(DownloadManager.EXTRA_DOWNLOAD_ID);
        if (downloadId != id) {
            return;
        }
        DownloadManager.Query q = new DownloadManager.Query();
        q.setFilterById(id);
        DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Cursor cursor = dm.query(q);
        if (!cursor.moveToFirst()) {
            return;
        }
        int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
        int reason = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_REASON));
        if (status == DownloadManager.STATUS_SUCCESSFUL || reason == DownloadManager.ERROR_FILE_ALREADY_EXISTS) {
            button.setText(R.string.details_install);
        } else {
            button.setText(R.string.details_download);
        }
        button.setEnabled(true);
    }
}
