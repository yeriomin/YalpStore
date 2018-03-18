package com.github.yeriomin.yalpstore.notification;

import android.app.IntentService;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.github.yeriomin.yalpstore.DownloadManagerFactory;
import com.github.yeriomin.yalpstore.DownloadManagerInterface;
import com.github.yeriomin.yalpstore.DownloadState;
import com.github.yeriomin.yalpstore.Paths;
import com.github.yeriomin.yalpstore.YalpStoreApplication;

import java.util.ArrayList;
import java.util.List;

public class CancelDownloadService extends IntentService {

    static public final String DOWNLOAD_ID = "DOWNLOAD_ID";
    static public final String PACKAGE_NAME = "PACKAGE_NAME";

    private DownloadManagerInterface dm;

    public CancelDownloadService() {
        super("CancelDownloadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        dm = DownloadManagerFactory.get(getApplicationContext());
        long downloadId = intent.getLongExtra(DOWNLOAD_ID, 0L);
        String packageName = intent.getStringExtra(PACKAGE_NAME);
        if (downloadId == 0 && TextUtils.isEmpty(packageName)) {
            Log.w(getClass().getSimpleName(), "No download id or package name provided in the intent");
        }
        List<Long> downloadIds = new ArrayList<>();
        if (downloadId != 0) {
            downloadIds.add(downloadId);
        }
        if (TextUtils.isEmpty(packageName)) {
            return;
        }
        ((YalpStoreApplication) getApplicationContext()).removePendingUpdate(packageName);
        DownloadState state = DownloadState.get(packageName);
        downloadIds.addAll(state.getDownloadIds());
        for (long id: downloadIds) {
            cancel(id);
        }
        if (null != state.getApp()) {
            Paths.getApkPath(getApplicationContext(), packageName, state.getApp().getVersionCode()).delete();
        }
        state.reset();
    }

    private void cancel(long downloadId) {
        Log.i(getClass().getSimpleName(), "Cancelling download " + downloadId);
        dm.cancel(downloadId);
    }
}
