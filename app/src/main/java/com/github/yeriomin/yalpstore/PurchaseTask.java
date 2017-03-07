package com.github.yeriomin.yalpstore;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;

import com.github.yeriomin.yalpstore.model.App;

public class PurchaseTask extends GoogleApiAsyncTask {

    static public final String URL_PURCHASE = "https://play.google.com/store/apps/details?id=";

    protected App app;

    public void setApp(App app) {
        this.app = app;
    }

    @Override
    protected Throwable doInBackground(String... params) {
        PlayStoreApiWrapper wrapper = new PlayStoreApiWrapper(context);
        try {
            new Downloader(context).download(app, wrapper.purchaseOrDeliver(app));
        } catch (Throwable e) {
            return e;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Throwable e) {
        super.onPostExecute(e);
        if (e instanceof NotPurchasedException && !DownloadState.get(app.getPackageName()).isBackground()) {
            getNotPurchasedDialog(context).show();
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
