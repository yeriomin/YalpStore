package in.dragons.galaxy;

import android.app.Application;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.http.HttpResponseCache;
import android.os.Handler;
import android.os.HandlerThread;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import in.dragons.galaxy.downloader.DownloadManagerInterface;

public class GalaxyApplication extends Application {

    private boolean isBackgroundUpdating = false;
    private List<String> pendingUpdates = new ArrayList<>();

    public boolean isBackgroundUpdating() {
        return isBackgroundUpdating;
    }

    public void setBackgroundUpdating(boolean backgroundUpdating) {
        isBackgroundUpdating = backgroundUpdating;
    }

    public void addPendingUpdate(String packageName) {
        pendingUpdates.add(packageName);
    }

    public void removePendingUpdate(String packageName) {
        removePendingUpdate(packageName, false);
    }

    public void removePendingUpdate(String packageName, boolean installed) {
        pendingUpdates.remove(packageName);
        Intent appIntent = new Intent(UpdateAllReceiver.ACTION_APP_UPDATE_COMPLETE);
        appIntent.putExtra(UpdateAllReceiver.EXTRA_PACKAGE_NAME, packageName);
        appIntent.putExtra(UpdateAllReceiver.EXTRA_UPDATE_ACTUALLY_INSTALLED, installed);
        sendBroadcast(appIntent, null);
        if (pendingUpdates.isEmpty()) {
            isBackgroundUpdating = false;
            Intent allIntent = new Intent(UpdateAllReceiver.ACTION_ALL_UPDATES_COMPLETE);
            sendBroadcast(allIntent, null);
        }
    }

    public void clearPendingUpdates() {
        pendingUpdates.clear();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            HttpResponseCache.install(new File(getCacheDir(), "http"), 5 * 1024 * 1024);
        } catch (IOException e) {
            Log.e(getClass().getSimpleName(), "Could not register cache " + e.getMessage());
        }

        PreferenceManager.setDefaultValues(this, R.xml.settings, false);
        registerDownloadReceiver();
        registerInstallReceiver();
    }

    private void registerDownloadReceiver() {
        HandlerThread handlerThread = new HandlerThread("handlerThread");
        handlerThread.start();
        IntentFilter filter = new IntentFilter();
        filter.addAction(DownloadManagerInterface.ACTION_DOWNLOAD_CANCELLED);
        filter.addAction(DownloadManagerInterface.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(new GlobalDownloadReceiver(), filter, null, new Handler(handlerThread.getLooper()));
    }

    private void registerInstallReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addDataScheme("package");
        filter.addAction(Intent.ACTION_INSTALL_PACKAGE);
        filter.addAction(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addAction(Intent.ACTION_PACKAGE_FULLY_REMOVED);
        filter.addAction(DetailsInstallReceiver.ACTION_PACKAGE_REPLACED_NON_SYSTEM);
        registerReceiver(new GlobalInstallReceiver(), filter);
    }

    public boolean isTv() {
        int uiMode = getResources().getConfiguration().uiMode;
        return (uiMode & Configuration.UI_MODE_TYPE_MASK) == Configuration.UI_MODE_TYPE_TELEVISION;
    }
}
