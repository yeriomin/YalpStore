package com.dragons.aurora.downloader;

import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build;

import com.dragons.aurora.playstoreapiv2.AndroidAppDeliveryData;
import com.dragons.aurora.playstoreapiv2.HttpCookie;

import java.io.File;

import com.dragons.aurora.model.App;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public abstract class DownloadRequestBuilder {

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
        if (deliveryData.getDownloadAuthCookieCount() > 0) {
            HttpCookie cookie = deliveryData.getDownloadAuthCookie(0);
            request.addRequestHeader("Cookie", cookie.getName() + "=" + cookie.getValue());
        }
        request.setDestinationUri(getDestinationUri());
        request.setDescription(app.getPackageName());
        request.setTitle(getNotificationTitle());
        return request;
    }

    private Uri getDestinationUri() {
        return Uri.fromFile(getDestinationFile());
    }

    private Uri getDownloadUri() {
        return Uri.parse(getDownloadUrl());
    }
}
