package com.github.yeriomin.yalpstore;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;

public class DetailsDownloadReceiver extends BroadcastReceiver {

    private Button button;

    public void setButton(Button button) {
        this.button = button;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        if (null == extras) {
            return;
        }
        long id = extras.getLong(DownloadManager.EXTRA_DOWNLOAD_ID);
        DownloadState state = DownloadState.get(id);
        if (null == state) {
            return;
        }
        state.setFinished(id);
        if (success(context, id)) {
            state.setSuccessful(id);
        }
        if (!state.isEverythingFinished()) {
            return;
        }
        if (state.isEverythingSuccessful()) {
            button.setText(R.string.details_install);
        } else {
            button.setText(R.string.details_download);
        }
        button.setEnabled(true);
    }

    private boolean success(Context context, long id) {
        DownloadManager.Query q = new DownloadManager.Query();
        q.setFilterById(id);
        DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Cursor cursor = dm.query(q);
        if (!cursor.moveToFirst()) {
            return false;
        }
        int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
        int reason = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_REASON));
        return status == DownloadManager.STATUS_SUCCESSFUL || reason == DownloadManager.ERROR_FILE_ALREADY_EXISTS;
    }
}
