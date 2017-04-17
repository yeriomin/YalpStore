package com.github.yeriomin.yalpstore;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.github.yeriomin.yalpstore.model.App;

import java.io.File;

public class DownloadBroadcastReceiver extends BroadcastReceiver {

    private Context context;
    private NotificationUtil notificationUtil;

    @Override
    public void onReceive(Context c, Intent i) {
        context = c;
        notificationUtil = new NotificationUtil(c);

        Bundle extras = i.getExtras();
        long downloadId = extras.getLong(DownloadManagerInterface.EXTRA_DOWNLOAD_ID);

        DownloadState state = DownloadState.get(downloadId);
        if (null == state) {
            return;
        }
        App app = state.getApp();

        DownloadManagerInterface dm = DownloadManagerFactory.get(context);
        if (!dm.finished(downloadId)) {
            return;
        }
        state.setFinished(downloadId);
        if (dm.success(downloadId)) {
            state.setSuccessful(downloadId);
        } else {
            String error = dm.getError(downloadId);
            Toast.makeText(context, error, Toast.LENGTH_LONG).show();
            notificationUtil.show(new Intent(), app.getDisplayName(), error);
        }

        if (state.isEverythingFinished() && state.isEverythingSuccessful()) {
            verifyAndInstall(app, state.isExplicitInstall());
        }
    }

    private void verifyAndInstall(App app, boolean explicitInstall) {
        if (shouldAutoInstall() || needToInstallUpdates() || (explicitInstall && PreferenceActivity.canInstallInBackground(context))) {
            InstallerAbstract installer = InstallerFactory.get(context);
            installer.verifyAndInstall(app);
        } else {
            notifyDownloadComplete(app);
        }
    }

    private void notifyDownloadComplete(App app) {
        notifyAndToast(
            R.string.notification_download_complete,
            R.string.notification_download_complete_toast,
            app
        );
    }

    private void notifyAndToast(int notificationStringId, int toastStringId, App app) {
        File file = Downloader.getApkPath(app.getPackageName(), app.getVersionCode());
        Intent openApkIntent = InstallerAbstract.getOpenApkIntent(context, file);
        notificationUtil.show(
            openApkIntent,
            app.getDisplayName(),
            context.getString(notificationStringId)
        );
        Toast.makeText(context, context.getString(toastStringId, app.getDisplayName()), Toast.LENGTH_LONG).show();
    }

    private boolean needToInstallUpdates() {
        return PreferenceActivity.getBoolean(context, PreferenceActivity.PREFERENCE_BACKGROUND_UPDATE_INSTALL);
    }

    private boolean shouldAutoInstall() {
        return PreferenceActivity.getBoolean(context, PreferenceActivity.PREFERENCE_AUTO_INSTALL);
    }
}
