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

package com.github.yeriomin.yalpstore.view;

import android.graphics.PorterDuff;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.yeriomin.yalpstore.DetailsActivity;
import com.github.yeriomin.yalpstore.R;
import com.github.yeriomin.yalpstore.Util;
import com.github.yeriomin.yalpstore.YalpStoreActivity;
import com.github.yeriomin.yalpstore.download.AppListProgressListener;
import com.github.yeriomin.yalpstore.download.DownloadManager;
import com.github.yeriomin.yalpstore.fragment.ButtonCancel;
import com.github.yeriomin.yalpstore.fragment.ButtonDownload;
import com.github.yeriomin.yalpstore.model.App;

import java.util.ArrayList;
import java.util.List;

public abstract class AppBadge extends ListItem {

    protected List<String> line2 = new ArrayList<>();
    protected List<String> line3 = new ArrayList<>();

    public App getApp() {
        return app;
    }

    @Override
    public void draw() {
        view.findViewById(R.id.more).setVisibility(View.GONE);
        view.findViewById(R.id.progress).setVisibility(View.GONE);
        view.findViewById(R.id.app).setVisibility(View.VISIBLE);
        LinearLayout backgroundDownloadProgress = view.findViewById(R.id.download_progress_container);
        if (null != backgroundDownloadProgress) {
            backgroundDownloadProgress.setVisibility(View.GONE);
        }

        ((TextView) view.findViewById(R.id.text1)).setText(app.getDisplayName());
        setText(R.id.text2, TextUtils.join(", ", line2));
        setText(R.id.text3, TextUtils.join(", ", line3));

        drawIcon((ImageView) view.findViewById(R.id.icon), app.getPackageName(), app.getIconInfo());
        redrawMoreButton();
    }

    protected void setText(int viewId, String text) {
        TextView textView = view.findViewById(viewId);
        if (!TextUtils.isEmpty(text)) {
            textView.setText(text);
            textView.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.GONE);
        }
    }

    public void redrawMoreButton() {
        if (null == view) {
            return;
        }
        final ButtonDownload buttonDownload = new ButtonDownload((YalpStoreActivity) view.getContext(), app);
        hideMoreButton();
        if (new ButtonCancel((YalpStoreActivity) view.getContext(), app).shouldBeVisible()) {
            enableCancelButton();
            DownloadManager.addProgressListener(app.getPackageName(), new AppListProgressListener(this));
        } else if (buttonDownload.shouldBeVisible()) {
            enableMoreButton(
                R.drawable.ic_download,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DetailsActivity.app = app;
                        buttonDownload.checkAndDownload();
                        enableCancelButton();
                    }
                }
            );
        }
    }

    public void hideMoreButton() {
        if (null == view) {
            return;
        }
        view.findViewById(R.id.more).setVisibility(View.GONE);
        ((TextView) view.findViewById(R.id.more_progress)).setText("");
        LinearLayout backgroundDownloadProgress = view.findViewById(R.id.download_progress_container);
        if (null != backgroundDownloadProgress) {
            backgroundDownloadProgress.setVisibility(View.GONE);
        }
    }

    public void setProgress(int progress, int max) {
        if (null == view) {
            return;
        }
        view.findViewById(R.id.more).setVisibility(View.VISIBLE);
        enableCancelButton();
        LinearLayout backgroundDownloadProgress = view.findViewById(R.id.download_progress_container);
        if (null != backgroundDownloadProgress) {
            backgroundDownloadProgress.setVisibility(View.VISIBLE);
            ((LinearLayout.LayoutParams) view.findViewById(R.id.download_progress).getLayoutParams()).weight = (int) (((float) progress/max)*100);
        }
        ((TextView) view.findViewById(R.id.more_progress)).setText(((int) (((float) progress/max)*100)) + "%");
    }

    protected void enableMoreButton(int drawableResId, View.OnClickListener listener) {
        if (null == view) {
            return;
        }
        LinearLayout more = view.findViewById(R.id.more);
        more.setVisibility(View.VISIBLE);
        more.setOnClickListener(listener);
        ImageView moreImage = more.findViewById(R.id.more_image);
        moreImage.setImageResource(drawableResId);
        moreImage.setColorFilter(Util.getColor(more.getContext(), android.R.attr.textColorSecondary), PorterDuff.Mode.SRC_IN);
    }

    protected void enableCancelButton() {
        if (null == view) {
            return;
        }
        enableMoreButton(
            R.drawable.ic_cancel,
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new DownloadManager(v.getContext()).cancel(app.getPackageName());
                    redrawMoreButton();
                }
            }
        );
    }
}
