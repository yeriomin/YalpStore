package com.github.yeriomin.yalpstore;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.WindowManager;

import com.github.yeriomin.playstoreapi.AuthException;

import java.io.IOException;

public class PurchaseTask extends DeliveryDataTask {

    static public final String URL_PURCHASE = "https://play.google.com/store/apps/details?id=";

    private DownloadState.TriggeredBy triggeredBy = DownloadState.TriggeredBy.DOWNLOAD_BUTTON;
    private OnDownloadProgressListener listener;

    public void setTriggeredBy(DownloadState.TriggeredBy triggeredBy) {
        this.triggeredBy = triggeredBy;
    }

    public void setOnDownloadProgressListener(OnDownloadProgressListener listener) {
        this.listener = listener;
    }

    @Override
    protected Throwable doInBackground(String... params) {
        DownloadState.get(app.getPackageName()).setTriggeredBy(triggeredBy);
        try {
            Throwable result = super.doInBackground(params);
            if (null != result) {
                return result;
            }
            if (null != deliveryData) {
                new Downloader(context).download(app, deliveryData, listener);
            } else {
                context.sendBroadcast(new Intent(DownloadManagerInterface.ACTION_DOWNLOAD_CANCELLED));
                Log.e(getClass().getName(), app.getPackageName() + " no download link returned");
            }
        } catch (Throwable e) {
            context.sendBroadcast(new Intent(DownloadManagerInterface.ACTION_DOWNLOAD_CANCELLED));
            return e;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Throwable e) {
        super.onPostExecute(e);
        if (e instanceof NotPurchasedException) {
            try {
                getNotPurchasedDialog(context).show();
            } catch (WindowManager.BadTokenException e1) {
                Log.e(getClass().getName(), "Could not create purchase error dialog: " + e1.getMessage());
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
                Log.w(getClass().getName(), app.getPackageName() + " not available");
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
