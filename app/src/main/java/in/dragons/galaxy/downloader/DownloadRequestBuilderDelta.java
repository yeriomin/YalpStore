package in.dragons.galaxy.downloader;

import android.content.Context;

import com.github.yeriomin.playstoreapi.AndroidAppDeliveryData;

import java.io.File;

import in.dragons.galaxy.Paths;
import in.dragons.galaxy.model.App;

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
