package com.github.yeriomin.yalpstore;

import com.github.yeriomin.yalpstore.model.App;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DownloadState {

    static private Map<String, DownloadState> state = new HashMap<>();
    static private Map<Long, String> downloadIds = new HashMap<>();

    private Set<Long> started = new HashSet<>();
    private Set<Long> finished = new HashSet<>();
    private Set<Long> successful = new HashSet<>();
    private App app;
    private boolean explicitInstall;

    static public DownloadState get(String packageName) {
        if (!state.containsKey(packageName)) {
            state.put(packageName, new DownloadState());
        }
        return state.get(packageName);
    }

    static public DownloadState get(long downloadId) {
        if (downloadIds.containsKey(downloadId)) {
            return get(downloadIds.get(downloadId));
        }
        return null;
    }

    public void setApp(App app) {
        this.app = app;
    }

    public App getApp() {
        return app;
    }

    public boolean isEverythingFinished() {
        return started.size() == finished.size();
    }

    public boolean isEverythingSuccessful() {
        return started.size() == successful.size();
    }

    public void setStarted(long downloadId) {
        started.add(downloadId);
        downloadIds.put(downloadId, app.getPackageName());
    }

    public void setFinished(long downloadId) {
        finished.add(downloadId);
    }

    public void setSuccessful(long downloadId) {
        successful.add(downloadId);
    }

    public boolean isExplicitInstall() {
        return explicitInstall;
    }

    public void setExplicitInstall(boolean explicitInstall) {
        this.explicitInstall = explicitInstall;
    }
}
