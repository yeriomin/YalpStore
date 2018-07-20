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

package com.github.yeriomin.yalpstore;

import android.view.View;
import android.widget.ProgressBar;

import java.lang.ref.WeakReference;

public class DownloadProgressBarUpdater extends DownloadProgressUpdater {

    private WeakReference<ProgressBar> progressBarRef = new WeakReference<>(null);

    public DownloadProgressBarUpdater(String packageName, ProgressBar progressBar) {
        super(packageName);
        progressBarRef = new WeakReference<>(progressBar);
    }

    @Override
    protected void setProgress(int progress, int max) {
        ProgressBar progressBar = progressBarRef.get();
        if (null == progressBar) {
            return;
        }
        progressBar.setIndeterminate(false);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(progress);
        progressBar.setMax(max);
    }

    @Override
    protected void finish() {
        ProgressBar progressBar = progressBarRef.get();
        if (null == progressBar) {
            return;
        }
        progressBar.setVisibility(View.INVISIBLE);
        progressBar.setIndeterminate(true);
    }
}
