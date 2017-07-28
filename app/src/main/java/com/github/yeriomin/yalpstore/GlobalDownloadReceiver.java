package com.github.yeriomin.yalpstore;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.github.yeriomin.yalpstore.model.App;
import com.github.yeriomin.yalpstore.notification.NotificationManagerWrapper;

import java.io.File;

public class GlobalDownloadReceiver extends BroadcastReceiver {

    private Context context;
    private NotificationManagerWrapper notificationManager;

    @Override
    public void onReceive(Context c, Intent i) {
        context = c;
        notificationManager = new NotificationManagerWrapper(c);

        long downloadId = i.getLongExtra(DownloadManagerInterface.EXTRA_DOWNLOAD_ID, 0L);
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
        if (i.getAction().equals(DownloadManagerInterface.ACTION_DOWNLOAD_CANCELLED)) {
            return;
        } else if (dm.success(downloadId)) {
            state.setSuccessful(downloadId);
        } else {
            String error = dm.getError(downloadId);
            Toast.makeText(context.getApplicationContext(), error, Toast.LENGTH_LONG).show();
            notificationManager.show(new Intent(), app.getDisplayName(), error);
        }

        if (!state.isEverythingFinished() || !state.isEverythingSuccessful()) {
            return;
        }
        if (isDelta(app)) {
            if (!DeltaPatcherFactory.get(app).patch()) {
                Log.e(getClass().getName(), "Delta patching failed for " + app.getPackageName());
                return;
            }
        }
        verifyAndInstall(app, state.getTriggeredBy());
    }

    private boolean isDelta(App app) {
        return !Paths.getApkPath(app.getPackageName(), app.getVersionCode()).exists()
            && Paths.getDeltaPath(app.getPackageName(), app.getVersionCode()).exists()
        ;
    }

    private void verifyAndInstall(App app, DownloadState.TriggeredBy triggeredBy) {
        boolean autoInstall = triggeredBy.equals(DownloadState.TriggeredBy.DOWNLOAD_BUTTON) && shouldAutoInstall();
        if (autoInstall
            || (
                needToInstallUpdates()
                && PreferenceActivity.canInstallInBackground(context)
                && (triggeredBy.equals(DownloadState.TriggeredBy.SCHEDULED_UPDATE)
                    || triggeredBy.equals(DownloadState.TriggeredBy.UPDATE_ALL_BUTTON)
                )
            )
        ) {
            InstallerAbstract installer = InstallerFactory.get(context);
            if (autoInstall) {
                installer.setBackground(false);
            }
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
        File file = Paths.getApkPath(app.getPackageName(), app.getVersionCode());
        Intent openApkIntent = InstallerAbstract.getOpenApkIntent(context, file);
        notificationManager.show(
            openApkIntent,
            app.getDisplayName(),
            context.getString(notificationStringId)
        );
        Toast.makeText(context.getApplicationContext(), context.getString(toastStringId, app.getDisplayName()), Toast.LENGTH_LONG).show();
    }

    private boolean needToInstallUpdates() {
        return PreferenceActivity.getBoolean(context, PreferenceActivity.PREFERENCE_BACKGROUND_UPDATE_INSTALL);
    }

    private boolean shouldAutoInstall() {
        return PreferenceActivity.getBoolean(context, PreferenceActivity.PREFERENCE_AUTO_INSTALL);
    }
}
