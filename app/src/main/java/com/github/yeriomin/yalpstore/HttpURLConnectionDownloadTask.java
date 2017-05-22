package com.github.yeriomin.yalpstore;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;

import com.github.yeriomin.yalpstore.model.App;
import com.github.yeriomin.yalpstore.notification.NotificationManagerFactory;

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
    private App app;
    private File targetFile;
    private long downloadId;

    private String getNotificationTitle() {
        if (targetFile.toString().endsWith(EXTENSION_OBB)) {
            return context.getString(R.string.expansion_file, app.getDisplayName());
        }
        return app.getDisplayName();
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setApp(App app) {
        this.app = app;
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
        NotificationManagerFactory.get(context).show(
            new Intent(),
            getNotificationTitle(),
            context.getString(R.string.notification_download_starting)
        );
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        NotificationManagerFactory.get(context).cancel(getNotificationTitle());
        Intent intent = new Intent();
        intent.setAction(DownloadManagerInterface.ACTION_DOWNLOAD_COMPLETE);
        intent.putExtra(DownloadManagerInterface.EXTRA_DOWNLOAD_ID, downloadId);
        context.sendBroadcast(intent);
    }

    @Override
    protected void onProgressUpdate(Long... values) {
        super.onProgressUpdate(values);
        NotificationManagerFactory.get(context).show(
            new Intent(),
            getNotificationTitle(),
            context.getString(
                R.string.notification_download_progress,
                Formatter.formatFileSize(context, values[0]),
                Formatter.formatFileSize(context, values[1])
            )
        );
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
        }
        try {
            in.close();
            out.close();
        } catch (IOException e) {
            // Could not close
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
                publishProgress(totalBytesRead, fileSize);
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
