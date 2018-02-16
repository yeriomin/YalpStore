package in.dragons.galaxy.notification;

import android.app.IntentService;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import in.dragons.galaxy.DownloadManagerFactory;
import in.dragons.galaxy.DownloadManagerInterface;
import in.dragons.galaxy.DownloadState;
import in.dragons.galaxy.GalaxyApplication;

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
        if (!TextUtils.isEmpty(packageName)) {
            ((GalaxyApplication) getApplicationContext()).removePendingUpdate(packageName);
            downloadIds.addAll(DownloadState.get(packageName).getDownloadIds());
        }
        for (long id : downloadIds) {
            cancel(id);
        }
    }

    private void cancel(long downloadId) {
        Log.i(getClass().getSimpleName(), "Cancelling download " + downloadId);
        dm.cancel(downloadId);
    }
}
