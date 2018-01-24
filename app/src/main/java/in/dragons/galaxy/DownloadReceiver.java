package in.dragons.galaxy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import in.dragons.galaxy.model.App;

abstract public class DownloadReceiver extends BroadcastReceiver {

    public final static String ACTION_DELTA_PATCHING_COMPLETE = "ACTION_DELTA_PATCHING_COMPLETE";

    protected Context context;
    protected long downloadId;
    protected DownloadState state;

    abstract protected void process(Context context, Intent intent);

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        downloadId = intent.getLongExtra(DownloadManagerInterface.EXTRA_DOWNLOAD_ID, 0L);
        Log.i(getClass().getSimpleName(), intent.getAction() + " (" + downloadId + ") received");
        if (downloadId == 0) {
            return;
        }
        state = DownloadState.get(downloadId);
        if (null != state) {
            process(context, intent);
        }
    }

    protected boolean isDelta(App app) {
        return null != app
            && !Paths.getApkPath(context, app.getPackageName(), app.getVersionCode()).exists()
            && Paths.getDeltaPath(context, app.getPackageName(), app.getVersionCode()).exists()
        ;
    }

    static protected boolean actionIs(Intent intent, String action) {
        return !TextUtils.isEmpty(intent.getAction()) && intent.getAction().equals(action);
    }
}
