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

package com.github.yeriomin.yalpstore.fragment.details;

import android.widget.ProgressBar;

import com.github.yeriomin.yalpstore.DetailsActivity;
import com.github.yeriomin.yalpstore.DetailsDownloadReceiver;
import com.github.yeriomin.yalpstore.DetailsInstallReceiver;
import com.github.yeriomin.yalpstore.DownloadProgressBarUpdater;
import com.github.yeriomin.yalpstore.R;
import com.github.yeriomin.yalpstore.model.App;

import static com.github.yeriomin.yalpstore.task.playstore.PurchaseTask.UPDATE_INTERVAL;

public class DownloadOrInstall extends Abstract {

    private DetailsDownloadReceiver downloadReceiver;
    private DetailsInstallReceiver installReceiver;

    public DownloadOrInstall(DetailsActivity activity, App app) {
        super(activity, app);
    }

    @Override
    public void draw() {
        new ButtonUninstall(activity, app).draw();
        new ButtonDownload(activity, app).draw();
        new ButtonBuy(activity, app).draw();
        new ButtonCancel(activity, app).draw();
        new ButtonInstall((DetailsActivity) activity, app).draw();
        new ButtonRun((DetailsActivity) activity, app).draw();
        new DownloadProgressBarUpdater(app.getPackageName(), (ProgressBar) activity.findViewById(R.id.download_progress)).execute(UPDATE_INTERVAL);
    }

    public void download() {
        new ButtonDownload(activity, app).download();
    }

    public void unregisterReceivers() {
        activity.unregisterReceiver(downloadReceiver);
        downloadReceiver = null;
        activity.unregisterReceiver(installReceiver);
        installReceiver = null;
    }

    public void registerReceivers() {
        if (null == downloadReceiver) {
            downloadReceiver = new DetailsDownloadReceiver((DetailsActivity) activity, app.getPackageName());
        }
        if (null == installReceiver) {
            installReceiver = new DetailsInstallReceiver((DetailsActivity) activity, app.getPackageName());
        }
    }
}
