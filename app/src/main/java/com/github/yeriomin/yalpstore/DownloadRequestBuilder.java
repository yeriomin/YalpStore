package com.github.yeriomin.yalpstore;

import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build;

import com.github.yeriomin.playstoreapi.AndroidAppDeliveryData;
import com.github.yeriomin.playstoreapi.HttpCookie;
import com.github.yeriomin.yalpstore.model.App;

import java.io.File;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public abstract class DownloadRequestBuilder {
    private static final String TAG = DownloadRequestBuilder.class.getSimpleName();
    protected Context context;
    protected App app;
    protected AndroidAppDeliveryData deliveryData;

    public DownloadRequestBuilder(Context context, App app, AndroidAppDeliveryData deliveryData) {
        this.context = context;
        this.app = app;
        this.deliveryData = deliveryData;
    }

    abstract protected File getDestinationFile();

    abstract protected String getNotificationTitle();

    abstract protected String getDownloadUrl();

    public DownloadManager.Request build() {
        DownloadManager.Request request = new DownloadManager.Request(getDownloadUri());
        String cookies = "";
        if (deliveryData.getDownloadAuthCookieCount() > 0) {
            HttpCookie cookie = deliveryData.getDownloadAuthCookie(0);
            cookies = cookie.getName() + "=" + cookie.getValue();
            request.addRequestHeader("Cookie", cookies);
        }
        request.setDestinationUri(getDestinationUri());
        // 描述里有下载包名
        request.setDescription(app.getPackageName());
        request.setTitle(getNotificationTitle());

        LogHelper.i(TAG, "设置 request 成功：title:"
                + getNotificationTitle() + ",url"
                + getDownloadUrl() + "\n,dest:"
                + getDestinationUri() + ",cook:" + cookies);
        return request;
    }

    private Uri getDestinationUri() {
        return Uri.fromFile(getDestinationFile());
    }

    private Uri getDownloadUri() {
        return Uri.parse(getDownloadUrl());
    }
}
