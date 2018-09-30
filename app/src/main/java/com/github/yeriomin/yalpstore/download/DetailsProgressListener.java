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

import android.text.format.Formatter;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.yeriomin.yalpstore.ContextUtil;
import com.github.yeriomin.yalpstore.DetailsActivity;
import com.github.yeriomin.yalpstore.R;
import com.github.yeriomin.yalpstore.download.DownloadManager.ProgressListener;

import java.lang.ref.WeakReference;

public class DetailsProgressListener implements ProgressListener {

    private WeakReference<DetailsActivity> activityRef;

    public DetailsProgressListener(DetailsActivity activity) {
        this.activityRef = new WeakReference<>(activity);
    }

    @Override
    public void onProgress(long bytesDownloaded, long bytesTotal) {
        if (null == activityRef.get()) {
            return;
        }
        LinearLayout progressContainer = activityRef.get().findViewById(R.id.download_progress_container);
        if (null != progressContainer) {
            progressContainer.setVisibility(View.VISIBLE);
            TextView progressTextView = activityRef.get().findViewById(R.id.download_progress_size);
            progressTextView.setText(activityRef.get().getString(
                R.string.notification_download_progress,
                Formatter.formatShortFileSize(activityRef.get(), bytesDownloaded),
                Formatter.formatShortFileSize(activityRef.get(), bytesTotal)
            ));
            activityRef.get().findViewById(R.id.download).setVisibility(View.GONE);
        }
        ProgressBar progressBar = activityRef.get().findViewById(R.id.download_progress);
        progressBar.setIndeterminate(false);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress((int) bytesDownloaded);
        progressBar.setMax((int) bytesTotal);
    }

    @Override
    public void onCompletion() {
        if (null == activityRef.get()) {
            return;
        }
        ContextUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LinearLayout progressContainer = activityRef.get().findViewById(R.id.download_progress_container);
                if (null != progressContainer) {
                    progressContainer.setVisibility(View.GONE);
                }
                ProgressBar progressBar = activityRef.get().findViewById(R.id.download_progress);
                progressBar.setVisibility(View.INVISIBLE);
                progressBar.setIndeterminate(true);
                progressBar.setProgress(0);
                activityRef.get().redrawButtons();
            }
        });
    }
}
