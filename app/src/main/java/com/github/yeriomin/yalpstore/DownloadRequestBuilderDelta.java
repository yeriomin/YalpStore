package com.github.yeriomin.yalpstore;

import com.github.yeriomin.playstoreapi.AndroidAppDeliveryData;
import com.github.yeriomin.yalpstore.model.App;

import java.io.File;

public class DownloadRequestBuilderDelta extends DownloadRequestBuilderApk {

    public DownloadRequestBuilderDelta(App app, AndroidAppDeliveryData deliveryData) {
        super(app, deliveryData);
    }

    @Override
    protected String getDownloadUrl() {
        return deliveryData.getPatchData().getDownloadUrl();
    }

    @Override
    protected File getDestinationFile() {
        return Paths.getDeltaPath(app.getPackageName(), app.getVersionCode());
    }
}
