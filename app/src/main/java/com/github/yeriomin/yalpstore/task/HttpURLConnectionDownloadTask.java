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

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;

import com.github.yeriomin.yalpstore.DownloadManagerFake;
import com.github.yeriomin.yalpstore.DownloadManagerInterface;
import com.github.yeriomin.yalpstore.DownloadState;
import com.github.yeriomin.yalpstore.R;
import com.github.yeriomin.yalpstore.Util;
import com.github.yeriomin.yalpstore.notification.CancelDownloadService;
import com.github.yeriomin.yalpstore.notification.NotificationBuilder;
import com.github.yeriomin.yalpstore.notification.NotificationManagerWrapper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import info.guardianproject.netcipher.NetCipher;

public class HttpURLConnectionDownloadTask extends AsyncTask<String, Long, Boolean> {

    static private final String EXTENSION_OBB = ".obb";
    static private final int PROGRESS_INTERVAL = 300;

    private Context context;
    private File targetFile;
    private long downloadId;
    private NotificationBuilder notificationBuilder;

    public void setContext(Context context) {
        this.context = context;
    }

    public void setTargetFile(File targetFile) {
        this.targetFile = targetFile;
    }

    public void setDownloadId(long downloadId) {
        this.downloadId = downloadId;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        new NotificationManagerWrapper(context).show(
            new Intent(),
            getNotificationTitle(),
            context.getString(R.string.notification_download_starting)
        );
    }

    @Override
    protected void onPostExecute(Boolean result) {
        new NotificationManagerWrapper(context).cancel(getNotificationTitle());
        Intent intent = new Intent();
        intent.setAction((null != result && result) ? DownloadManagerInterface.ACTION_DOWNLOAD_COMPLETE : DownloadManagerInterface.ACTION_DOWNLOAD_CANCELLED);
        intent.putExtra(DownloadManagerInterface.EXTRA_DOWNLOAD_ID, downloadId);
        context.sendBroadcast(intent);
    }

    @Override
    protected void onProgressUpdate(Long... values) {
        super.onProgressUpdate(values);
        DownloadState state = DownloadState.get(downloadId);
        if (null != state) {
            state.setProgress(downloadId, values[0].intValue(), values[1].intValue());
        }
        notifyProgress(values[0], values[1]);
    }

    @Override
    protected void onCancelled() {
        Log.i(getClass().getSimpleName(), "Cancelled download " + downloadId);
        targetFile.delete();
        onPostExecute(false);
    }

    @Override
    protected Boolean doInBackground(String... params) {
        HttpURLConnection connection;
        InputStream in;
        long fileSize;
        try {
            connection = NetCipher.getHttpURLConnection(params[0]);
            if (params.length == 2 && !TextUtils.isEmpty(params[1])) {
                connection.addRequestProperty("Cookie", params[1]);
            }
            in = connection.getInputStream();
            fileSize = connection.getContentLength();
        } catch (IOException e) {
            DownloadManagerFake.putStatus(downloadId, DownloadManagerInterface.ERROR_HTTP_DATA_ERROR);
            return false;
        }

        byte[] checksum = writeToFile(in, fileSize);
        if (null == checksum) {
            DownloadManagerFake.putStatus(downloadId, DownloadManagerInterface.ERROR_FILE_ERROR);
            return false;
        }
        if (targetFile.getAbsolutePath().endsWith(".apk")) {
            DownloadState.get(downloadId).setApkChecksum(checksum);
        }
        connection.disconnect();

        DownloadManagerFake.putStatus(downloadId, DownloadManagerInterface.SUCCESS);
        return true;
    }

    private void notifyProgress(long progress, long max) {
        String title = getNotificationTitle();
        if (null == notificationBuilder) {
            notificationBuilder = NotificationManagerWrapper.getBuilder(context)
                .setTitle(title)
                .setIntent(new Intent())
                .addAction(R.drawable.ic_cancel, android.R.string.cancel, getCancelIntent())
            ;
        }
        notificationBuilder
            .setMessage(context.getString(
                R.string.notification_download_progress,
                Formatter.formatFileSize(context, progress),
                Formatter.formatFileSize(context, max)
            ))
            .setProgress((int) max, (int) progress)
        ;
        new NotificationManagerWrapper(context).show(
            title,
            notificationBuilder.build()
        );
    }

    private PendingIntent getCancelIntent() {
        Intent intentCancel = new Intent(context, CancelDownloadService.class);
        intentCancel.putExtra(CancelDownloadService.DOWNLOAD_ID, downloadId);
        return PendingIntent.getService(context, 0, intentCancel, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private String getNotificationTitle() {
        String fileName = targetFile.getName();
        String displayName = DownloadState.get(downloadId).getApp().getDisplayName();
        if (fileName.endsWith(EXTENSION_OBB)) {
            return context.getString(
                fileName.startsWith("main") ? R.string.expansion_file_main : R.string.expansion_file_patch,
                displayName
            );
        }
        return displayName;
    }

    private byte[] writeToFile(InputStream in, long fileSize) {
        OutputStream out;
        try {
            out = new FileOutputStream(targetFile);
        } catch (FileNotFoundException e) {
            //  Should be checked before launching this task
            return null;
        }

        try {
            return copyStream(in, out, fileSize);
        } catch (IOException | IllegalStateException e) {
            Log.e(getClass().getSimpleName(), "Could not read: " + e.getMessage());
            DownloadManagerFake.putStatus(downloadId, DownloadManagerInterface.ERROR_HTTP_DATA_ERROR);
            Util.closeSilently(out);
            targetFile.delete();
            return null;
        } finally {
            Util.closeSilently(in);
            Util.closeSilently(out);
        }
    }

    private byte[] copyStream(InputStream in, OutputStream out, long fileSize) throws IOException {
        byte[] buffer = new byte[2048];
        int bytesRead;
        long totalBytesRead = 0;
        long lastProgressUpdate = 0;

        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
        while ((bytesRead = in.read(buffer)) != -1) {
            md.update(buffer, 0, bytesRead);
            totalBytesRead += bytesRead;
            if (lastProgressUpdate + PROGRESS_INTERVAL < System.currentTimeMillis()) {
                lastProgressUpdate = System.currentTimeMillis();
                if (DownloadState.get(downloadId).isCancelled(downloadId)) {
                    cancel(true);
                    return null;
                } else {
                    publishProgress(totalBytesRead, fileSize);
                }
            }
            try {
                out.write(buffer, 0, bytesRead);
            } catch (IOException e) {
                Log.e(getClass().getSimpleName(), "Could not write file: " + e.getMessage());
                DownloadManagerFake.putStatus(downloadId, DownloadManagerInterface.ERROR_FILE_ERROR);
                Util.closeSilently(out);
                targetFile.delete();
                return null;
            }
        }
        return md.digest();
    }
}
