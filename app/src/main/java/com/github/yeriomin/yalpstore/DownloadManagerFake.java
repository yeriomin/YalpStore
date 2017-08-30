package com.github.yeriomin.yalpstore;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import com.github.yeriomin.playstoreapi.AndroidAppDeliveryData;
import com.github.yeriomin.playstoreapi.HttpCookie;
import com.github.yeriomin.yalpstore.model.App;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class DownloadManagerFake extends DownloadManagerAbstract {

    static private final Map<Long, Integer> statuses = new HashMap<>();

    static public void putStatus(long downloadId, int status) {
        statuses.put(downloadId, status);
    }

    public DownloadManagerFake(Context context) {
        super(context);
    }

    @Override
    public long enqueue(App app, AndroidAppDeliveryData deliveryData, Type type, OnDownloadProgressListener listener) {
        Log.i(getClass().getName(), "Downloading " + type.name() + " for " + app.getPackageName());
        String url = getUrl(deliveryData, type);
        long downloadId = url.hashCode();
        statuses.put(downloadId, DownloadManagerInterface.IN_PROGRESS);
        DownloadState.get(app.getPackageName()).setStarted(downloadId);
        if (!enoughSpace(deliveryData)) {
            statuses.put(downloadId, DownloadManagerInterface.ERROR_INSUFFICIENT_SPACE);
        } else {
            HttpURLConnectionDownloadTask task = new HttpURLConnectionDownloadTask();
            task.setContext(context);
            task.setDownloadId(downloadId);
            task.setTargetFile(getDestinationFile(app, type));
            task.setOnDownloadProgressListener(listener);
            String cookieString = null;
            if (deliveryData.getDownloadAuthCookieCount() > 0) {
                HttpCookie cookie = deliveryData.getDownloadAuthCookie(0);
                cookieString = cookie.getName() + "=" + cookie.getValue();
            }
            task.execute(url, cookieString);
        }
        return downloadId;
    }

    @Override
    public boolean finished(long downloadId) {
        return statuses.containsKey(downloadId) && statuses.get(downloadId) != DownloadManagerInterface.IN_PROGRESS;
    }

    @Override
    public boolean success(long downloadId) {
        return statuses.containsKey(downloadId) && statuses.get(downloadId) == DownloadManagerInterface.SUCCESS;
    }

    @Override
    public String getError(long downloadId) {
        return null;
    }

    private boolean enoughSpace(AndroidAppDeliveryData deliveryData) {
        long bytesNeeded = 0;
        bytesNeeded += deliveryData.getDownloadSize();
        if (deliveryData.getAdditionalFileCount() == 2) {
            bytesNeeded += deliveryData.getAdditionalFile(1).getSize();
        } else if (deliveryData.getAdditionalFileCount() == 1) {
            bytesNeeded += deliveryData.getAdditionalFile(0).getSize();
        }
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        long bytesAvailable = (long) stat.getBlockSize() * (long) stat.getBlockCount();
        return bytesAvailable >= bytesNeeded;
    }

    private String getUrl(AndroidAppDeliveryData deliveryData, Type type) {
        switch (type) {
            case APK:
                return deliveryData.getDownloadUrl();
            case DELTA:
                return deliveryData.getPatchData().getDownloadUrl();
            case OBB_MAIN:
                return deliveryData.getAdditionalFile(0).getDownloadUrl();
            case OBB_PATCH:
                return deliveryData.getAdditionalFile(1).getDownloadUrl();
            default:
                throw new RuntimeException("Unknown request type");
        }
    }

    private File getDestinationFile(App app, Type type) {
        switch (type) {
            case APK:
                return Paths.getApkPath(context, app.getPackageName(), app.getVersionCode());
            case DELTA:
                return Paths.getDeltaPath(context, app.getPackageName(), app.getVersionCode());
            case OBB_MAIN:
                return Paths.getObbPath(app.getPackageName(), app.getVersionCode(), true);
            case OBB_PATCH:
                return Paths.getObbPath(app.getPackageName(), app.getVersionCode(), false);
            default:
                throw new RuntimeException("Unknown request type");
        }
    }
}
