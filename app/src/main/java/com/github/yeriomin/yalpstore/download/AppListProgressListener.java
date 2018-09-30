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

package com.github.yeriomin.yalpstore.download;

import com.github.yeriomin.yalpstore.ContextUtil;
import com.github.yeriomin.yalpstore.view.AppBadge;

import java.lang.ref.WeakReference;

public class AppListProgressListener implements DownloadManager.ProgressListener {

    private WeakReference<AppBadge> appBadgeRef;

    public AppListProgressListener(AppBadge appBadge) {
        this.appBadgeRef = new WeakReference<>(appBadge);
    }

    @Override
    public void onProgress(long bytesDownloaded, long bytesTotal) {
        if (null == appBadgeRef.get()) {
            return;
        }
        appBadgeRef.get().setProgress((int) bytesDownloaded, (int) bytesTotal);
    }

    @Override
    public void onCompletion() {
        if (null == appBadgeRef.get()) {
            return;
        }
        ContextUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                appBadgeRef.get().redrawMoreButton();
            }
        });
    }
}
