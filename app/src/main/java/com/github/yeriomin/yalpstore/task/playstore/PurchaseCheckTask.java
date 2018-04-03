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

import android.view.View;
import android.widget.Button;

import com.github.yeriomin.playstoreapi.AndroidAppDeliveryData;
import com.github.yeriomin.yalpstore.R;
import com.github.yeriomin.yalpstore.fragment.details.DownloadOrInstall;

import java.util.Timer;

public class PurchaseCheckTask extends DeliveryDataTask {

    private DownloadOrInstall downloadOrInstallFragment;
    private Button downloadButton;
    private Timer timer;

    public void setDownloadOrInstallFragment(DownloadOrInstall downloadOrInstallFragment) {
        this.downloadOrInstallFragment = downloadOrInstallFragment;
    }

    public void setTimer(Timer timer) {
        this.timer = timer;
    }

    public void setDownloadButton(Button downloadButton) {
        this.downloadButton = downloadButton;
    }

    @Override
    protected void onPostExecute(AndroidAppDeliveryData deliveryData) {
        boolean success = success() && null != deliveryData;
        downloadOrInstallFragment.draw();
        if (null == downloadButton) {
            return;
        }
        downloadButton.setText(success ? R.string.details_download : R.string.details_download_not_available);
        downloadButton.setEnabled(success);
        downloadButton.setVisibility(View.VISIBLE);
        timer.cancel();
    }
}
