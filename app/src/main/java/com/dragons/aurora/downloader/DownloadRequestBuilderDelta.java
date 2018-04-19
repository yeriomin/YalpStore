package com.dragons.aurora.downloader;

import android.content.Context;

import com.dragons.aurora.playstoreapiv2.AndroidAppDeliveryData;

import java.io.File;

import com.dragons.aurora.Paths;
import com.dragons.aurora.model.App;

public class DownloadRequestBuilderDelta extends DownloadRequestBuilderApk {

    public DownloadRequestBuilderDelta(Context context, App app, AndroidAppDeliveryData deliveryData) {
        super(context, app, deliveryData);
    }

    @Override
    protected String getDownloadUrl() {
        return deliveryData.getPatchData().getDownloadUrl();
    }

    @Override
    protected File getDestinationFile() {
        return Paths.getDeltaPath(context, app.getPackageName(), app.getVersionCode());
    }
}
