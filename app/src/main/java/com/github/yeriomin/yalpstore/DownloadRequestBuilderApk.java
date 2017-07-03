package com.github.yeriomin.yalpstore;

import com.github.yeriomin.playstoreapi.AndroidAppDeliveryData;
import com.github.yeriomin.yalpstore.model.App;

import java.io.File;

public class DownloadRequestBuilderApk extends DownloadRequestBuilder {

    public DownloadRequestBuilderApk(App app, AndroidAppDeliveryData deliveryData) {
        super(app, deliveryData);
    }

    @Override
    protected String getDownloadUrl() {
        return deliveryData.getDownloadUrl();
    }

    @Override
    protected File getDestinationFile() {
        return Paths.getApkPath(app.getPackageName(), app.getVersionCode());
    }

    @Override
    protected String getNotificationTitle() {
        return app.getDisplayName();
    }
}
