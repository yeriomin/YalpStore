package com.github.yeriomin.yalpstore;

import android.content.Context;

import com.github.yeriomin.playstoreapi.AndroidAppDeliveryData;
import com.github.yeriomin.yalpstore.model.App;

import java.io.File;

public class DownloadRequestBuilderObb extends DownloadRequestBuilder {

    private boolean main;
    private Context context;

    public DownloadRequestBuilderObb(App app, AndroidAppDeliveryData deliveryData) {
        super(app, deliveryData);
    }

    public DownloadRequestBuilderObb setMain(boolean main) {
        this.main = main;
        return this;
    }

    public DownloadRequestBuilderObb setContext(Context context) {
        this.context = context;
        return this;
    }

    @Override
    protected File getDestinationFile() {
        return Downloader.getObbPath(
            app.getPackageName(),
            deliveryData.getAdditionalFile(main ? 0 : 1).getVersionCode(),
            main
        );
    }

    @Override
    protected String getDownloadUrl() {
        return deliveryData.getAdditionalFile(main ? 0 : 1).getDownloadUrl();
    }

    @Override
    protected String getNotificationTitle() {
        return context.getString(main ? R.string.expansion_file_main : R.string.expansion_file_patch, app.getDisplayName());
    }
}
