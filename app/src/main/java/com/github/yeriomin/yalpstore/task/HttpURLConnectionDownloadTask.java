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
import com.github.yeriomin.yalpstore.OnDownloadProgressListener;
import com.github.yeriomin.yalpstore.R;
import com.github.yeriomin.yalpstore.Util;
import com.github.yeriomin.yalpstore.notification.CancelDownloadService;
import com.github.yeriomin.yalpstore.notification.NotificationManagerWrapper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpURLConnectionDownloadTask extends AsyncTask<String, Long, Boolean> {

    static private final String EXTENSION_OBB = ".obb";
    static private final int PROGRESS_INTERVAL = 300;

    private Context context;
    private File targetFile;
    private long downloadId;
    private OnDownloadProgressListener listener;

    public void setContext(Context context) {
        this.context = context;
    }

    public void setTargetFile(File targetFile) {
        this.targetFile = targetFile;
    }

    public void setDownloadId(long downloadId) {
        this.downloadId = downloadId;
    }

    public void setOnDownloadProgressListener(OnDownloadProgressListener listener) {
        this.listener = listener;
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
        super.onPostExecute(result);
        new NotificationManagerWrapper(context).cancel(getNotificationTitle());
        Intent intent = new Intent();
        intent.setAction((null != result && result) ? DownloadManagerInterface.ACTION_DOWNLOAD_COMPLETE : DownloadManagerInterface.ACTION_DOWNLOAD_CANCELLED);
        intent.putExtra(DownloadManagerInterface.EXTRA_DOWNLOAD_ID, downloadId);
        context.sendBroadcast(intent);
    }

    @Override
    protected void onProgressUpdate(Long... values) {
        super.onProgressUpdate(values);
        DownloadState.get(downloadId).setProgress(downloadId, values[0].intValue(), values[1].intValue());
        if (null != listener) {
            listener.onDownloadProgress();
        }
        notifyProgress(values[0], values[1]);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        Log.i(getClass().getName(), "Cancelled download " + downloadId);
        targetFile.delete();
        onPostExecute(false);
    }

    @Override
    protected Boolean doInBackground(String... params) {
        HttpURLConnection connection;
        InputStream in;
        long fileSize;
        try {
            connection = (HttpURLConnection) new URL(params[0]).openConnection();
            if (params.length == 2 && !TextUtils.isEmpty(params[1])) {
                connection.addRequestProperty("Cookie", params[1]);
            }
            in = connection.getInputStream();
            fileSize = connection.getContentLength();
        } catch (IOException e) {
            DownloadManagerFake.putStatus(downloadId, DownloadManagerInterface.ERROR_HTTP_DATA_ERROR);
            return false;
        }

        if (!writeToFile(in, fileSize)) {
            return false;
        }
        connection.disconnect();
        DownloadManagerFake.putStatus(downloadId, DownloadManagerInterface.SUCCESS);
        return true;
    }

    private void notifyProgress(long progress, long max) {
        String title = getNotificationTitle();
        new NotificationManagerWrapper(context).show(
            title,
            NotificationManagerWrapper.getBuilder(context)
                .setMessage(context.getString(
                    R.string.notification_download_progress,
                    Formatter.formatFileSize(context, progress),
                    Formatter.formatFileSize(context, max)
                ))
                .setTitle(title)
                .setIntent(new Intent())
                .setProgress((int) max, (int) progress)
                .addAction(android.R.drawable.ic_menu_close_clear_cancel, android.R.string.cancel, getCancelIntent())
                .build()
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

    private boolean writeToFile(InputStream in, long fileSize) {
        OutputStream out;
        try {
            out = new FileOutputStream(targetFile);
        } catch (FileNotFoundException e) {
            //  Should be checked before launching this task
            return false;
        }

        try {
            copyStream(in, out, fileSize);
        } catch (IOException e) {
            Log.e(getClass().getName(), "Could not read: " + e.getMessage());
            DownloadManagerFake.putStatus(downloadId, DownloadManagerInterface.ERROR_HTTP_DATA_ERROR);
            return false;
        } finally {
            Util.closeSilently(in);
            Util.closeSilently(out);
        }
        return true;
    }

    private void copyStream(InputStream in, OutputStream out, long fileSize) throws IOException {
        byte[] buffer = new byte[2048];
        int bytesRead;
        long totalBytesRead = 0;
        long lastProgressUpdate = 0;
        while ((bytesRead = in.read(buffer)) != -1) {
            totalBytesRead += bytesRead;
            if (lastProgressUpdate + PROGRESS_INTERVAL < System.currentTimeMillis()) {
                lastProgressUpdate = System.currentTimeMillis();
                if (DownloadState.get(downloadId).isCancelled(downloadId)) {
                    cancel(false);
                    return;
                } else {
                    publishProgress(totalBytesRead, fileSize);
                }
            }
            try {
                out.write(buffer, 0, bytesRead);
            } catch (IOException e) {
                Log.e(getClass().getName(), "Could not write file: " + e.getMessage());
                DownloadManagerFake.putStatus(downloadId, DownloadManagerInterface.ERROR_FILE_ERROR);
                return;
            }
        }
    }
}
