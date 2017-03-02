package com.github.yeriomin.yalpstore;

import android.app.DownloadManager;
import android.net.Uri;

import com.github.yeriomin.playstoreapi.AndroidAppDeliveryData;
import com.github.yeriomin.playstoreapi.HttpCookie;
import com.github.yeriomin.yalpstore.model.App;

import java.io.File;

public abstract class DownloadRequestBuilder {

    App app;
    AndroidAppDeliveryData deliveryData;

    public DownloadRequestBuilder(App app, AndroidAppDeliveryData deliveryData) {
        this.app = app;
        this.deliveryData = deliveryData;
    }

    abstract protected File getDestinationFile();

    abstract protected String getDownloadUrl();

    public DownloadManager.Request build() {
        DownloadManager.Request request = new DownloadManager.Request(getDownloadUri());
        if (deliveryData.getDownloadAuthCookieCount() > 0) {
            HttpCookie cookie = deliveryData.getDownloadAuthCookie(0);
            request.addRequestHeader("Cookie", cookie.getName() + "=" + cookie.getValue());
        }
        request.setDestinationUri(getDestinationUri());
        request.setDescription(app.getPackageName());
        request.setTitle(app.getDisplayName());
        return request;
    }

    private Uri getDestinationUri() {
        return Uri.fromFile(getDestinationFile());
    }

    private Uri getDownloadUri() {
        return Uri.parse(getDownloadUrl().replace("https", "http"));
    }
}
