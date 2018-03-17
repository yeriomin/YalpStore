package in.dragons.galaxy.downloader;

import android.content.Context;

import com.github.yeriomin.playstoreapi.AndroidAppDeliveryData;

import java.io.File;

import in.dragons.galaxy.Paths;
import in.dragons.galaxy.model.App;

public class DownloadRequestBuilderApk extends DownloadRequestBuilder {

    public DownloadRequestBuilderApk(Context context, App app, AndroidAppDeliveryData deliveryData) {
        super(context, app, deliveryData);
    }

    @Override
    protected String getDownloadUrl() {
        return deliveryData.getDownloadUrl();
    }

    @Override
    protected File getDestinationFile() {
        return Paths.getApkPath(context, app.getPackageName(), app.getVersionCode());
    }

    @Override
    protected String getNotificationTitle() {
        return app.getDisplayName();
    }
}
