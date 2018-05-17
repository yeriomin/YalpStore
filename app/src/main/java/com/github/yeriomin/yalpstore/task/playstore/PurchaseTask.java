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

package com.github.yeriomin.yalpstore.task.playstore;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.WindowManager;

import com.github.yeriomin.playstoreapi.AndroidAppDeliveryData;
import com.github.yeriomin.playstoreapi.AuthException;
import com.github.yeriomin.playstoreapi.GooglePlayAPI;
import com.github.yeriomin.yalpstore.ContextUtil;
import com.github.yeriomin.yalpstore.DownloadManagerInterface;
import com.github.yeriomin.yalpstore.DownloadProgressUpdater;
import com.github.yeriomin.yalpstore.DownloadProgressUpdaterFactory;
import com.github.yeriomin.yalpstore.DownloadState;
import com.github.yeriomin.yalpstore.Downloader;
import com.github.yeriomin.yalpstore.NotPurchasedException;
import com.github.yeriomin.yalpstore.R;
import com.github.yeriomin.yalpstore.YalpStoreActivity;
import com.github.yeriomin.yalpstore.notification.CancelDownloadService;
import com.github.yeriomin.yalpstore.view.PurchaseDialogBuilder;

import java.io.IOException;

public class PurchaseTask extends DeliveryDataTask implements CloneableTask {

    static public final long UPDATE_INTERVAL = 300;

    protected DownloadState.TriggeredBy triggeredBy = DownloadState.TriggeredBy.DOWNLOAD_BUTTON;

    @Override
    public CloneableTask clone() {
        PurchaseTask task = new PurchaseTask();
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
                    if (context instanceof YalpStoreActivity) {
                        DownloadProgressUpdater progressUpdater = DownloadProgressUpdaterFactory.get((YalpStoreActivity) context, app.getPackageName());
                        if (null != progressUpdater) {
                            progressUpdater.execute(UPDATE_INTERVAL);
                        }
                    }
                } else {
                    sendCancelBroadcast();
                    Log.e(getClass().getSimpleName(), app.getPackageName() + " not enough storage space");
                    throw new IOException(context.getString(R.string.download_manager_ERROR_INSUFFICIENT_SPACE));
                }
            } catch (IllegalArgumentException | SecurityException e) {
                sendCancelBroadcast();
                Log.e(getClass().getSimpleName(), app.getPackageName() + " unknown storage error: " + e.getClass().getName() + ": " + e.getMessage());
                throw new IOException(context.getString(R.string.download_manager_ERROR_FILE_ERROR));
            }
        } else {
            sendCancelBroadcast();
            Log.e(getClass().getSimpleName(), app.getPackageName() + " no download link returned");
        }
        return deliveryData;
    }

    @Override
    protected void processException(Throwable e) {
        super.processException(e);
        sendCancelBroadcast();
    }

    private void sendCancelBroadcast() {
        DownloadState.get(app.getPackageName()).setApp(app);
        Intent intentCancel = new Intent(context, CancelDownloadService.class);
        intentCancel.putExtra(CancelDownloadService.PACKAGE_NAME, app.getPackageName());
        context.startService(intentCancel);
        context.sendBroadcast(new Intent(DownloadManagerInterface.ACTION_DOWNLOAD_CANCELLED).putExtra(Intent.EXTRA_PACKAGE_NAME, app.getPackageName()));
    }

    @Override
    protected void onPostExecute(AndroidAppDeliveryData deliveryData) {
        super.onPostExecute(deliveryData);
        if (getException() instanceof NotPurchasedException
            && ContextUtil.isAlive(context)
            && triggeredBy.equals(DownloadState.TriggeredBy.DOWNLOAD_BUTTON)
            && triggeredBy.equals(DownloadState.TriggeredBy.MANUAL_DOWNLOAD_BUTTON)
        ) {
            try {
                new PurchaseDialogBuilder((Activity) context, app.getPackageName()).show();
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
}
