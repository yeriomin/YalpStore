package com.github.yeriomin.yalpstore;

import android.content.Context;
import android.os.Environment;

import com.github.yeriomin.playstoreapi.AndroidAppDeliveryData;
import com.github.yeriomin.playstoreapi.AppFileMetadata;
import com.github.yeriomin.yalpstore.model.App;

import java.io.File;

public class Downloader {

    private DownloadManagerInterface dm;

    static public File getApkPath(String packageName, int version) {
        File downloadsDir = new File(Environment.getExternalStorageDirectory(), "Download");
        String filename = packageName + "." + String.valueOf(version) + ".apk";
        return new File(downloadsDir, filename);
    }

    static public File getObbPath(String packageName, int version, boolean main) {
        File obbDir = new File(new File(Environment.getExternalStorageDirectory(), "Android/obb"), packageName);
        String filename = (main ? "main" : "patch") + "." + String.valueOf(version) + "." + packageName + ".obb";
        return new File(obbDir, filename);
    }

    public Downloader(Context context) {
        this.dm = DownloadManagerFactory.get(context);
    }

    public void download(App app, AndroidAppDeliveryData deliveryData) {
        DownloadState state = DownloadState.get(app.getPackageName());
        state.setApp(app);
        state.setStarted(dm.enqueue(app, deliveryData, DownloadManagerInterface.Type.APK));
        if (deliveryData.getAdditionalFileCount() > 0) {
            checkAndStartObbDownload(state, deliveryData, true);
        }
        if (deliveryData.getAdditionalFileCount() > 1) {
            checkAndStartObbDownload(state, deliveryData, false);
        }
    }

    private void checkAndStartObbDownload(DownloadState state, AndroidAppDeliveryData deliveryData, boolean main) {
        App app = state.getApp();
        AppFileMetadata metadata = deliveryData.getAdditionalFile(main ? 0 : 1);
        File file = getObbPath(app.getPackageName(), metadata.getVersionCode(), main);
        if (file.exists() && file.length() != metadata.getSize()) {
            file.delete();
        }
        file.getParentFile().mkdirs();
        if (!file.exists()) {
            state.setStarted(dm.enqueue(
                app,
                deliveryData,
                main ? DownloadManagerInterface.Type.OBB_MAIN : DownloadManagerInterface.Type.OBB_PATCH
            ));
        }
    }
}
