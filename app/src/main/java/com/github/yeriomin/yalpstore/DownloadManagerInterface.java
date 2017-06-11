package com.github.yeriomin.yalpstore;

import com.github.yeriomin.playstoreapi.AndroidAppDeliveryData;
import com.github.yeriomin.yalpstore.model.App;

public interface DownloadManagerInterface {

    String EXTRA_DOWNLOAD_ID = "extra_download_id";
    String ACTION_DOWNLOAD_COMPLETE = "android.intent.action.DOWNLOAD_COMPLETE";

    int SUCCESS = 1;
    int IN_PROGRESS = 0;
    int ERROR_UNKNOWN = 1000;
    int ERROR_FILE_ERROR = 1001;
    int ERROR_UNHANDLED_HTTP_CODE = 1002;
    int ERROR_HTTP_DATA_ERROR = 1004;
    int ERROR_TOO_MANY_REDIRECTS = 1005;
    int ERROR_INSUFFICIENT_SPACE = 1006;
    int ERROR_DEVICE_NOT_FOUND = 1007;
    int ERROR_CANNOT_RESUME = 1008;
    int ERROR_FILE_ALREADY_EXISTS = 1009;
    int ERROR_BLOCKED = 1010;

    enum Type {
        APK, OBB_MAIN, OBB_PATCH
    }

    long enqueue(App app, AndroidAppDeliveryData deliveryData, Type type, OnDownloadProgressListener listener);
    boolean finished(long downloadId);
    boolean success(long downloadId);
    String getError(long downloadId);
}
