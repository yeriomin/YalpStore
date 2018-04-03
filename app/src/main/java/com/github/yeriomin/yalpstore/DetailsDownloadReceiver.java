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

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

public class DetailsDownloadReceiver extends ForegroundDownloadReceiver {

    private String packageName;

    public DetailsDownloadReceiver(DetailsActivity activity, String packageName) {
        super(activity);
        this.packageName = packageName;
    }

    @Override
    protected void process(Context context, Intent intent) {
        if (!state.getApp().getPackageName().equals(packageName)) {
            return;
        }
        super.process(context, intent);
    }

    protected void draw() {
        cleanup();
        if (!state.isEverythingSuccessful()) {
            return;
        }
        activityRef.get().findViewById(R.id.download).setVisibility(View.GONE);
        activityRef.get().findViewById(R.id.install).setVisibility(View.VISIBLE);
        boolean installing = !state.getTriggeredBy().equals(DownloadState.TriggeredBy.MANUAL_DOWNLOAD_BUTTON)
            && (PreferenceUtil.getBoolean(activityRef.get(), PreferenceUtil.PREFERENCE_AUTO_INSTALL)
                || PreferenceUtil.getBoolean(activityRef.get(), PreferenceUtil.PREFERENCE_DOWNLOAD_INTERNAL_STORAGE)
            )
        ;
        toggle(R.id.install, installing ? R.string.details_installing : R.string.details_install, !installing);
    }

    protected void cleanup() {
        ProgressBar progressBar = activityRef.get().findViewById(R.id.download_progress);
        if (null != progressBar) {
            progressBar.setVisibility(View.INVISIBLE);
            progressBar.setProgress(0);
        }
        View buttonCancel = activityRef.get().findViewById(R.id.cancel);
        if (null != buttonCancel) {
            buttonCancel.setVisibility(View.GONE);
        }
        toggle(R.id.download, R.string.details_download, true);
    }

    private void toggle(int buttonId, int stringResId, boolean enable) {
        View button = activityRef.get().findViewById(buttonId);
        if (null == button) {
            return;
        }
        button.setEnabled(enable);
        if (button instanceof Button) {
            ((Button) button).setText(stringResId);
        }
    }
}
