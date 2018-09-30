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

package com.github.yeriomin.yalpstore.task;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.github.yeriomin.yalpstore.NetworkUtil;
import com.github.yeriomin.yalpstore.Util;
import com.github.yeriomin.yalpstore.download.DownloadManager;
import com.github.yeriomin.yalpstore.download.Request;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.util.zip.GZIPInputStream;

public class DownloadTask extends LowCpuIntensityTask<String, Long, DownloadTask.DownloadException> {

    static private final int PROGRESS_INTERVAL = 300;

    private WeakReference<Context> contextRef = new WeakReference<>(null);
    private Request request;

    private boolean paused;

    public boolean isPaused() {
        return paused;
    }

    public void resume() {
        paused = false;
    }

    private void pause() {
        paused = true;
        while (paused) {
            sleep();
        }
    }

    public void setContext(Context context) {
        this.contextRef = new WeakReference<>(context.getApplicationContext());
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    @Override
    protected void onProgressUpdate(Long... values) {
        super.onProgressUpdate(values);
        DownloadManager.setBytesDownloaded(request.getPackageName(), request.getTypeName(), values[0].intValue());
    }

    @Override
    protected void onPreExecute() {
        DownloadManager.setRunning(request.getPackageName(), request.getTypeName(), true);
    }

    @Override
    protected DownloadException doInBackground(String... params) {
        while (true) {
            if (DownloadManager.isCancelled(request.getPackageName())) {
                Log.i(getClass().getSimpleName(), "Cancelled " + request.getPackageName() + " " + request.getType());
                cancel(false);
                return null;
            }
            try {
                Log.i(getClass().getSimpleName(), "Downloading " + request.getPackageName() + " " + request.getType() + " to " + request.getDestination());
                start();
                if (!isCancelled()) {
                    Log.i(getClass().getSimpleName(), "Successfully downloaded " + request.getPackageName() + " " + request.getType() + " to " + request.getDestination());
                }
                return null;
            } catch (NoNetworkException e) {
                Log.w(getClass().getSimpleName(), "Network connectivity lost, pausing " + request.getPackageName() + " " + request.getType());
                pause();
            } catch (DownloadException e) {
                Log.e(getClass().getSimpleName(), "Could not download " + request.getPackageName() + " " + request.getType() + ": " + e.getMessage());
                return e;
            }
        }
    }

    @Override
    protected void onPostExecute(DownloadException exception) {
        DownloadManager.setRunning(request.getPackageName(), request.getTypeName(), false);
        DownloadManager dm = new DownloadManager(contextRef.get());
        if (null != exception) {
            dm.error(request.getPackageName(), exception.getError());
        } else {
            if (Request.Type.DELTA.equals(request.getType())) {
                dm.patch(request.getPackageName());
            } else {
                dm.complete(request.getPackageName(), request.getTypeName());
            }
        }
    }

    private void start() throws DownloadException {
        HttpURLConnection connection;
        InputStream in;
        try {
            connection = NetworkUtil.getHttpURLConnection(request.getUrl());
            if (!TextUtils.isEmpty(request.getCookieString())) {
                connection.addRequestProperty("Cookie", request.getCookieString());
            }
            if (request.getDestination().exists()) {
                connection.setRequestProperty("Range", "Bytes=" + request.getDestination().length() + "-");
            }
            in = connection.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
            throw new DownloadException("Could not open network connection: " + e.getMessage(), DownloadManager.Error.HTTP_DATA_ERROR);
        }
        try {
            writeToFile(in);
        } finally {
            connection.disconnect();
        }
    }

    private void writeToFile(InputStream in) throws DownloadException {
        OutputStream out;
        try {
            out = new FileOutputStream(request.getDestination(), request.getDestination().exists());
        } catch (FileNotFoundException e) {
            //  Should be checked before launching this task
            throw new DownloadException(e.getClass().getSimpleName() + " while opening output stream", DownloadManager.Error.FILE_ERROR);
        }
        try {
            if (request.isGzipped()) {
                in = new GZIPInputStream(in);
            }
            copyStream(in, out, request.getDestination().exists() ? request.getDestination().length() : 0);
        } catch (IOException | IllegalStateException e) {
            e.printStackTrace();
            sleep(PROGRESS_INTERVAL*3);
            Util.closeSilently(out);
            if (NetworkUtil.isNetworkAvailable(contextRef.get()) && NetworkUtil.internetAccessPresent()) {
                throw new DownloadException(e.getClass().getSimpleName() + " happened, but network is available: " + e.getMessage(), DownloadManager.Error.HTTP_DATA_ERROR);
            } else {
                throw new NoNetworkException();
            }
        } finally {
            Util.closeSilently(in);
            Util.closeSilently(out);
        }
    }

    private void copyStream(InputStream in, OutputStream out, long totalBytesRead) throws IOException {
        byte[] buffer = new byte[2048];
        int bytesRead;
        long lastProgressUpdate = 0;
        while ((bytesRead = in.read(buffer)) != -1) {
            totalBytesRead += bytesRead;
            if (lastProgressUpdate + PROGRESS_INTERVAL < System.currentTimeMillis()) {
                lastProgressUpdate = System.currentTimeMillis();
                publishProgress(totalBytesRead, request.getSize());
                if (isCancelled() || DownloadManager.isCancelled(request.getPackageName())) {
                    Log.i(getClass().getSimpleName(), "Cancelled " + request.getPackageName() + " " + request.getType());
                    return;
                }
            }
            try {
                out.write(buffer, 0, bytesRead);
            } catch (IOException e) {
                e.printStackTrace();
                throw new DownloadException("Could not write file: " + e.getMessage(), DownloadManager.Error.FILE_ERROR);
            }
        }
        publishProgress(request.getSize(), request.getSize());
    }

    static private void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            // Nothing to do
        }
    }

    static private void sleep() {
        sleep(PROGRESS_INTERVAL);
    }

    static public class DownloadException extends IOException {

        private DownloadManager.Error error;

        public DownloadManager.Error getError() {
            return error;
        }

        public DownloadException(String message, DownloadManager.Error error) {
            super(message);
            this.error = error;
        }
    }

    static private class NoNetworkException extends DownloadException {

        public NoNetworkException() {
            this("");
        }

        public NoNetworkException(String message) {
            super(message, null);
        }
    }
}
