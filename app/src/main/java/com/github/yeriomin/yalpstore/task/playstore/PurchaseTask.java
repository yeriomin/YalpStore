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

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.view.WindowManager;

import com.github.yeriomin.playstoreapi.AndroidAppDeliveryData;
import com.github.yeriomin.playstoreapi.AuthException;
import com.github.yeriomin.playstoreapi.GooglePlayAPI;
import com.github.yeriomin.yalpstore.ContextUtil;
import com.github.yeriomin.yalpstore.NotPurchasedException;
import com.github.yeriomin.yalpstore.R;
import com.github.yeriomin.yalpstore.YalpStorePermissionManager;
import com.github.yeriomin.yalpstore.download.DownloadManager;
import com.github.yeriomin.yalpstore.download.State;
import com.github.yeriomin.yalpstore.task.DownloadTask;
import com.github.yeriomin.yalpstore.view.PurchaseDialogBuilder;

import java.io.IOException;

public class PurchaseTask extends DeliveryDataTask implements CloneableTask {

    protected State.TriggeredBy triggeredBy = State.TriggeredBy.DOWNLOAD_BUTTON;

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

    public void setTriggeredBy(State.TriggeredBy triggeredBy) {
        this.triggeredBy = triggeredBy;
    }

    @Override
    protected AndroidAppDeliveryData getResult(GooglePlayAPI api, String... arguments) throws IOException {
        if (DownloadManager.isCancelled(app.getPackageName())) {
            Log.e(getClass().getSimpleName(), app.getPackageName() + " is cancelled before it even started");
            return deliveryData;
        }
        super.getResult(api, arguments);
        DownloadManager dm = new DownloadManager(context);
        if (null == deliveryData) {
            Log.e(getClass().getSimpleName(), app.getPackageName() + " no download link returned");
            dm.error(app.getPackageName(), DownloadManager.Error.CANNOT_RESUME);
            return deliveryData;
        }
        if (context instanceof Activity
            && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
            && deliveryData.getAdditionalFileCount() > 0
            && context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e(getClass().getSimpleName(), app.getPackageName() + " needs obb files, so we need WRITE_EXTERNAL_STORAGE permission even if internal storage is used for apks");
            new YalpStorePermissionManager((Activity) context).requestPermission();
            return deliveryData;
        }
        if (DownloadManager.isCancelled(app.getPackageName())) {
            Log.e(getClass().getSimpleName(), app.getPackageName() + " is cancelled before it even started");
            return deliveryData;
        }
        try {
            dm.start(app, deliveryData, triggeredBy);
        } catch (IllegalArgumentException | SecurityException e) {
            Log.e(getClass().getSimpleName(), app.getPackageName() + " unknown storage error: " + e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
            throw new DownloadTask.DownloadException(context.getString(R.string.download_manager_ERROR_FILE_ERROR), DownloadManager.Error.FILE_ERROR);
        }
        return deliveryData;
    }

    @Override
    protected void processException(Throwable e) {
        super.processException(e);
        new DownloadManager(context).error(
            app.getPackageName(),
            e instanceof DownloadTask.DownloadException
                ? ((DownloadTask.DownloadException) e).getError()
                : DownloadManager.Error.UNKNOWN
        );
    }

    @Override
    protected void onPostExecute(AndroidAppDeliveryData deliveryData) {
        super.onPostExecute(deliveryData);
        if (getException() instanceof NotPurchasedException
            && ContextUtil.isAlive(context)
            && (triggeredBy.equals(State.TriggeredBy.DOWNLOAD_BUTTON)
                || triggeredBy.equals(State.TriggeredBy.MANUAL_DOWNLOAD_BUTTON)
            )
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
