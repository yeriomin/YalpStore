package com.github.yeriomin.yalpstore.task.playstore;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.WindowManager;

import com.github.yeriomin.playstoreapi.AndroidAppDeliveryData;
import com.github.yeriomin.playstoreapi.AuthException;
import com.github.yeriomin.playstoreapi.GooglePlayAPI;
import com.github.yeriomin.yalpstore.ContextUtil;
import com.github.yeriomin.yalpstore.DownloadManagerInterface;
import com.github.yeriomin.yalpstore.DownloadProgressBarUpdater;
import com.github.yeriomin.yalpstore.DownloadState;
import com.github.yeriomin.yalpstore.Downloader;
import com.github.yeriomin.yalpstore.NotPurchasedException;
import com.github.yeriomin.yalpstore.R;

import java.io.IOException;

public class PurchaseTask extends DeliveryDataTask implements CloneableTask {

    static public final String URL_PURCHASE = "https://play.google.com/store/apps/details?id=";
    static public final long UPDATE_INTERVAL = 300;

    protected DownloadState.TriggeredBy triggeredBy = DownloadState.TriggeredBy.DOWNLOAD_BUTTON;
    protected DownloadProgressBarUpdater progressBarUpdater;

    @Override
    public CloneableTask clone() {
        PurchaseTask task = new PurchaseTask();
        task.setDownloadProgressBarUpdater(progressBarUpdater);
        task.setTriggeredBy(triggeredBy);
        task.setApp(app);
        task.setErrorView(errorView);
        task.setContext(context);
        task.setProgressIndicator(progressIndicator);
        return task;
    }

    public void setTriggeredBy(DownloadState.TriggeredBy triggeredBy) {
        this.triggeredBy = triggeredBy;
    }

    public void setDownloadProgressBarUpdater(DownloadProgressBarUpdater progressBarUpdater) {
        this.progressBarUpdater = progressBarUpdater;
    }

    @Override
    protected AndroidAppDeliveryData getResult(GooglePlayAPI api, String... arguments) throws IOException {
        DownloadState state = DownloadState.get(app.getPackageName());
        if (null != state) {
            state.setTriggeredBy(triggeredBy);
        }
        super.getResult(api, arguments);
        if (null != deliveryData) {
            Downloader downloader = new Downloader(context);
            try {
                if (downloader.enoughSpace(deliveryData)) {
                    downloader.download(app, deliveryData);
                    if (null != progressBarUpdater) {
                        progressBarUpdater.execute(UPDATE_INTERVAL);
                    }
                } else {
                    context.sendBroadcast(new Intent(DownloadManagerInterface.ACTION_DOWNLOAD_CANCELLED));
                    Log.e(getClass().getSimpleName(), app.getPackageName() + " not enough storage space");
                    throw new IOException(context.getString(R.string.download_manager_ERROR_INSUFFICIENT_SPACE));
                }
            } catch (IllegalArgumentException | SecurityException e) {
                context.sendBroadcast(new Intent(DownloadManagerInterface.ACTION_DOWNLOAD_CANCELLED));
                Log.e(getClass().getSimpleName(), app.getPackageName() + " unknown storage error: " + e.getClass().getName() + ": " + e.getMessage());
                throw new IOException(context.getString(R.string.download_manager_ERROR_FILE_ERROR));
            }
        } else {
            context.sendBroadcast(new Intent(DownloadManagerInterface.ACTION_DOWNLOAD_CANCELLED));
            Log.e(getClass().getSimpleName(), app.getPackageName() + " no download link returned");
        }
        return deliveryData;
    }

    @Override
    protected void processException(Throwable e) {
        super.processException(e);
        context.sendBroadcast(new Intent(DownloadManagerInterface.ACTION_DOWNLOAD_CANCELLED));
    }

    @Override
    protected void onPostExecute(AndroidAppDeliveryData deliveryData) {
        super.onPostExecute(deliveryData);
        if (getException() instanceof NotPurchasedException
            && triggeredBy.equals(DownloadState.TriggeredBy.DOWNLOAD_BUTTON)
            && triggeredBy.equals(DownloadState.TriggeredBy.MANUAL_DOWNLOAD_BUTTON)
        ) {
            try {
                getNotPurchasedDialog(context).show();
            } catch (WindowManager.BadTokenException e1) {
                Log.e(getClass().getSimpleName(), "Could not create purchase error dialog: " + e1.getMessage());
            }
        }
    }

    @Override
    protected void processIOException(IOException e) {
        if (!(e instanceof NotPurchasedException)) {
            super.processIOException(e);
        }
    }

    @Override
    protected void processAuthException(AuthException e) {
        if (e.getCode() == 403) {
            if (ContextUtil.isAlive(context)) {
                ContextUtil.toast(context, R.string.details_download_not_available);
            } else {
                Log.w(getClass().getSimpleName(), app.getPackageName() + " not available");
            }
        } else {
            super.processAuthException(e);
        }
    }

    private AlertDialog getNotPurchasedDialog(Context c) {
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder
            .setMessage(R.string.error_not_purchased)
            .setPositiveButton(
                android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(URL_PURCHASE + app.getPackageName()));
                        context.startActivity(i);
                    }
                }
            )
            .setNegativeButton(
                android.R.string.cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                }
            )
        ;
        return builder.create();
    }
}
