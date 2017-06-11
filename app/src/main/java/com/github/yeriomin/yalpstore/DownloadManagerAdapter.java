package com.github.yeriomin.yalpstore;

import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.util.Pair;

import com.github.yeriomin.playstoreapi.AndroidAppDeliveryData;
import com.github.yeriomin.yalpstore.model.App;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class DownloadManagerAdapter extends DownloadManagerAbstract {

    private DownloadManager dm;

    public DownloadManagerAdapter(Context context) {
        super(context);
        dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
    }

    @Override
    public long enqueue(App app, AndroidAppDeliveryData deliveryData, Type type, OnDownloadProgressListener listener) {
        DownloadManager.Request request;
        switch (type) {
            case APK:
                request = new DownloadRequestBuilderApk(app, deliveryData).build();
                break;
            case OBB_MAIN:
                request = new DownloadRequestBuilderObb(app, deliveryData).setContext(context).setMain(true).build();
                break;
            case OBB_PATCH:
                request = new DownloadRequestBuilderObb(app, deliveryData).setContext(context).setMain(false).build();
                break;
            default:
                throw new RuntimeException("Unknown request type");
        }
        long downloadId = dm.enqueue(request);
        if (null != listener) {
            DownloadManagerProgressUpdater updater = new DownloadManagerProgressUpdater(downloadId, this, listener);
            updater.update();
        }
        return downloadId;
    }

    @Override
    public boolean finished(long downloadId) {
        return null != getCursor(downloadId);
    }

    @Override
    public boolean success(long downloadId) {
        Cursor cursor = getCursor(downloadId);
        if (null == cursor) {
            return false;
        }
        int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
        int reason = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_REASON));
        cursor.close();
        return status == DownloadManager.STATUS_SUCCESSFUL || reason == DownloadManager.ERROR_FILE_ALREADY_EXISTS;
    }

    @Override
    public String getError(long downloadId) {
        Cursor cursor = getCursor(downloadId);
        if (null == cursor) {
            return getErrorString(context, DownloadManagerInterface.ERROR_UNKNOWN);
        }
        int reason = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_REASON));
        cursor.close();
        return getErrorString(context, reason);
    }

    public Pair<Integer, Integer> getProgress(long downloadId) {
        Cursor cursor = getCursor(downloadId);
        if (null == cursor) {
            return null;
        }
        int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
        int reason = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_REASON));
        int total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
        int complete = status == DownloadManager.STATUS_SUCCESSFUL || reason == DownloadManager.ERROR_FILE_ALREADY_EXISTS
            ? total
            : cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
        ;
        cursor.close();
        return new Pair<>(complete, total);
    }

    private Cursor getCursor(long downloadId) {
        Cursor cursor = dm.query(new DownloadManager.Query().setFilterById(downloadId));
        if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        return cursor;
    }
}
