package com.github.yeriomin.yalpstore;

import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;

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
    public long enqueue(App app, AndroidAppDeliveryData deliveryData, Type type) {
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
        return dm.enqueue(request);
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

    private Cursor getCursor(long downloadId) {
        Cursor cursor = dm.query(new DownloadManager.Query().setFilterById(downloadId));
        if (!cursor.moveToFirst()) {
            return null;
        }
        return cursor;
    }
}
