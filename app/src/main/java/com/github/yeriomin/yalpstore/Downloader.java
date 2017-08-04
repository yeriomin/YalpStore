package com.github.yeriomin.yalpstore;

import android.content.Context;
import android.util.Log;

import com.github.yeriomin.playstoreapi.AndroidAppDeliveryData;
import com.github.yeriomin.playstoreapi.AppFileMetadata;
import com.github.yeriomin.yalpstore.model.App;

import java.io.File;

public class Downloader {

    private DownloadManagerInterface dm;

    public Downloader(Context context) {
        this.dm = DownloadManagerFactory.get(context);
    }

    public void download(App app, AndroidAppDeliveryData deliveryData, OnDownloadProgressListener listener) {
        DownloadState state = DownloadState.get(app.getPackageName());
        state.setApp(app);
        DownloadManagerInterface.Type type = shouldDownloadDelta(app, deliveryData)
            ? DownloadManagerInterface.Type.DELTA
            : DownloadManagerInterface.Type.APK
        ;
        state.setStarted(dm.enqueue(app, deliveryData, type, listener));
        if (deliveryData.getAdditionalFileCount() > 0) {
            checkAndStartObbDownload(state, deliveryData, true, listener);
        }
        if (deliveryData.getAdditionalFileCount() > 1) {
            checkAndStartObbDownload(state, deliveryData, false, listener);
        }
    }

    private void checkAndStartObbDownload(DownloadState state, AndroidAppDeliveryData deliveryData, boolean main, OnDownloadProgressListener listener) {
        App app = state.getApp();
        AppFileMetadata metadata = deliveryData.getAdditionalFile(main ? 0 : 1);
        File file = Paths.getObbPath(app.getPackageName(), metadata.getVersionCode(), main);
        prepare(file, metadata.getSize());
        if (!file.exists()) {
            state.setStarted(dm.enqueue(
                app,
                deliveryData,
                main ? DownloadManagerInterface.Type.OBB_MAIN : DownloadManagerInterface.Type.OBB_PATCH,
                listener
            ));
        }
    }

    static private void prepare(File file, long expectedSize) {
        Log.i(Downloader.class.getName(), "file.exists()=" + file.exists() + " file.length()=" + file.length() + " metadata.getSize()=" + expectedSize);
        if (file.exists() && file.length() != expectedSize) {
            Log.i(Downloader.class.getName(), "Deleted old obb file: " + file.delete());
        }
        file.getParentFile().mkdirs();
    }

    static private boolean shouldDownloadDelta(App app, AndroidAppDeliveryData deliveryData) {
        File currentApk = InstalledApkCopier.getCurrentApk(app);
        return app.getVersionCode() > app.getInstalledVersionCode()
            && deliveryData.hasPatchData()
            && null != currentApk
            && currentApk.exists()
        ;
    }
}
