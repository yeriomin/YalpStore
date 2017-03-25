package com.github.yeriomin.yalpstore;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;

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
    public long enqueue(App app, AndroidAppDeliveryData deliveryData, Type type) {
        String url;
        File destinationFile;
        switch (type) {
            case APK:
                url = deliveryData.getDownloadUrl();
                destinationFile = Downloader.getApkPath(app.getPackageName(), app.getVersionCode());
                break;
            case OBB_MAIN:
                url = deliveryData.getAdditionalFile(0).getDownloadUrl();
                destinationFile = Downloader.getApkPath(app.getPackageName(), app.getVersionCode());
                break;
            case OBB_PATCH:
                url = deliveryData.getAdditionalFile(1).getDownloadUrl();
                destinationFile = Downloader.getApkPath(app.getPackageName(), app.getVersionCode());
                break;
            default:
                throw new RuntimeException("Unknown request type");
        }
        long downloadId = url.hashCode();
        statuses.put(downloadId, DownloadManagerInterface.IN_PROGRESS);
        if (!enoughSpace(deliveryData)) {
            statuses.put(downloadId, DownloadManagerInterface.ERROR_INSUFFICIENT_SPACE);
        } else {
            HttpURLConnectionDownloadTask task = new HttpURLConnectionDownloadTask();
            task.setContext(context);
            task.setApp(app);
            task.setDownloadId(downloadId);
            task.setTargetFile(destinationFile);
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
}
