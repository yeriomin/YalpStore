package com.github.yeriomin.yalpstore;

import android.util.Pair;

import com.github.yeriomin.yalpstore.model.App;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DownloadState {

    public enum TriggeredBy {
        DOWNLOAD_BUTTON,
        UPDATE_ALL_BUTTON,
        SCHEDULED_UPDATE,
        MANUAL_DOWNLOAD_BUTTON
    }

    enum Status {
        STARTED,
        FINISHED,
        SUCCESSFUL,
        CANCELLED,
    }

    static private Map<String, DownloadState> state = new HashMap<>();
    static private Map<Long, String> downloadIds = new HashMap<>();

    private App app;
    private TriggeredBy triggeredBy = TriggeredBy.DOWNLOAD_BUTTON;
    private Map<Long, Pair<Integer, Integer>> progress = new HashMap<>();
    private Map<Long, Status> status = new HashMap<>();
    private byte[] apkChecksum;

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
        boolean isEverythingFinished = true;
        for (Long downloadId: status.keySet()) {
            if (status.get(downloadId).equals(Status.STARTED)) {
                isEverythingFinished = false;
                break;
            }
        }
        return isEverythingFinished;
    }

    public boolean isEverythingSuccessful() {
        boolean isEverythingSuccessful = true;
        for (Long downloadId: status.keySet()) {
            if (!status.get(downloadId).equals(Status.SUCCESSFUL)) {
                isEverythingSuccessful = false;
                break;
            }
        }
        return isEverythingSuccessful;
    }

    public void setStarted(long downloadId) {
        status.put(downloadId, Status.STARTED);
        downloadIds.put(downloadId, app.getPackageName());
    }

    public byte[] getApkChecksum() {
        return apkChecksum;
    }

    public void setApkChecksum(byte[] apkChecksum) {
        this.apkChecksum = apkChecksum;
    }

    public void setFinished(long downloadId) {
        status.put(downloadId, Status.FINISHED);
    }

    public void setSuccessful(long downloadId) {
        status.put(downloadId, Status.SUCCESSFUL);
    }

    public void setCancelled(long downloadId) {
        status.put(downloadId, Status.CANCELLED);
    }

    public boolean isCancelled(long downloadId) {
        Status status = this.status.get(downloadId);
        return null != status && status.equals(Status.CANCELLED);
    }

    public List<Long> getDownloadIds() {
        List<Long> ids = new ArrayList<>();
        for (Long id: downloadIds.keySet()) {
            if (null != app && null != app.getPackageName() && app.getPackageName().equals(downloadIds.get(id))) {
                ids.add(id);
            }
        }
        return ids;
    }

    public TriggeredBy getTriggeredBy() {
        return triggeredBy;
    }

    public void setTriggeredBy(TriggeredBy triggeredBy) {
        this.triggeredBy = triggeredBy;
    }

    public Pair<Integer, Integer> getProgress() {
        int complete = 0;
        int total = 0;
        for (long downloadId: status.keySet()) {
            Pair<Integer, Integer> current = progress.get(downloadId);
            if (null == current) {
                continue;
            }
            complete += current.first;
            total += current.second;
        }
        return new Pair<>(complete, total);
    }

    public void setProgress(long downloadId, int complete, int total) {
        progress.put(downloadId, new Pair<>(complete, total));
    }
}
