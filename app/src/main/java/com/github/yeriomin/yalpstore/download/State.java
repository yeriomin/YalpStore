/*
 * Yalp Store
 * Copyright (C) 2018 Sergey Yeriomin <yeriomin@gmail.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.github.yeriomin.yalpstore.download;

import com.github.yeriomin.yalpstore.model.App;
import com.github.yeriomin.yalpstore.task.DownloadTask;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Holds transient parameters of a running download
 *
 */
public class State {

    public enum TriggeredBy {
        DOWNLOAD_BUTTON,
        UPDATE_ALL_BUTTON,
        SCHEDULED_UPDATE,
        MANUAL_DOWNLOAD_BUTTON
    }

    private App app;
    private TriggeredBy triggeredBy;
    private Map<String, File> files = new HashMap<>();
    private Set<DownloadManager.ProgressListener> progressListeners = new HashSet<>();
    private boolean cancelled = false;

    public App getApp() {
        return app;
    }

    public void setApp(App app) {
        this.app = app;
    }

    public TriggeredBy getTriggeredBy() {
        return triggeredBy;
    }

    public void setTriggeredBy(TriggeredBy triggeredBy) {
        this.triggeredBy = triggeredBy;
    }

    public boolean hasSplits() {
        for (File request: files.values()) {
            if (request.getRequest() instanceof RequestSplit) {
                return true;
            }
        }
        return false;
    }

    public Map<String, File> getFiles() {
        return files;
    }

    public File getFile(String type) {
        return files.get(type);
    }

    public void putFile(String type, File fileRequest) {
        files.put(type, fileRequest);
    }

    public File getFile(Request.Type type) {
        return files.get(type.name());
    }

    public long getBytesDownloaded() {
        long downloaded = 0;
        for (File file: files.values()) {
            downloaded += file.getBytesDownloaded();
        }
        return downloaded;
    }

    public void setBytesDownloaded(String type, long bytesDownloaded) {
        files.get(type).setBytesDownloaded(bytesDownloaded);
        for (DownloadManager.ProgressListener observer: progressListeners) {
            observer.onProgress(getBytesDownloaded(), getBytesTotal());
        }
    }

    public void complete() {
        for (DownloadManager.ProgressListener observer: progressListeners) {
            observer.onCompletion();
        }
    }

    public long getBytesTotal() {
        long total = 0;
        for (File file: files.values()) {
            total += file.getRequest().getSize();
        }
        return total;
    }

    public void addProgressListener(DownloadManager.ProgressListener listener) {
        Iterator<DownloadManager.ProgressListener> iterator = progressListeners.iterator();
        while (iterator.hasNext()) {
            DownloadManager.ProgressListener current = iterator.next();
            if (current.getClass().isInstance(listener)) {
                progressListeners.remove(current);
                break;
            }
        }
        progressListeners.add(listener);
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public static class File {

        private Request request;
        private DownloadTask task;
        private long bytesDownloaded;
        private boolean success;
        private boolean running;
        private Set<DownloadManager.ProgressListener> progressListeners = new HashSet<>();

        public Request getRequest() {
            return request;
        }

        public void setRequest(Request request) {
            this.request = request;
        }

        public DownloadTask getTask() {
            return task;
        }

        public void setTask(DownloadTask task) {
            this.task = task;
        }

        public long getBytesDownloaded() {
            return bytesDownloaded;
        }

        public void setBytesDownloaded(long bytesDownloaded) {
            this.bytesDownloaded = bytesDownloaded;
            for (DownloadManager.ProgressListener observer: progressListeners) {
                observer.onProgress(bytesDownloaded, request.getSize());
            }
        }

        public boolean isRunning() {
            return running;
        }

        public void setRunning(boolean running) {
            this.running = running;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess() {
            this.success = true;
            task = null;
            for (DownloadManager.ProgressListener observer: progressListeners) {
                observer.onCompletion();
            }
        }

        public void setProgressListener(DownloadManager.ProgressListener listener) {
            progressListeners.add(listener);
        }
    }
}
