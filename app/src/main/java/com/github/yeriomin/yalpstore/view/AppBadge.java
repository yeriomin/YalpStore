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

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.yeriomin.yalpstore.DetailsActivity;
import com.github.yeriomin.yalpstore.ListItemDownloadProgressUpdater;
import com.github.yeriomin.yalpstore.R;
import com.github.yeriomin.yalpstore.YalpStoreActivity;
import com.github.yeriomin.yalpstore.fragment.details.ButtonCancel;
import com.github.yeriomin.yalpstore.fragment.details.ButtonDownload;
import com.github.yeriomin.yalpstore.model.App;
import com.github.yeriomin.yalpstore.notification.CancelDownloadService;
import com.github.yeriomin.yalpstore.task.LoadImageTask;
import com.github.yeriomin.yalpstore.task.playstore.PurchaseTask;

import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

public abstract class AppBadge extends ListItem {

    static private WeakHashMap<Integer, LoadImageTask> tasks = new WeakHashMap<>();

    protected App app;
    protected List<String> line2 = new ArrayList<>();
    protected List<String> line3 = new ArrayList<>();

    public void setApp(App app) {
        this.app = app;
    }

    public App getApp() {
        return app;
    }

    @Override
    public void draw() {
        view.findViewById(R.id.more).setVisibility(View.GONE);
        view.findViewById(R.id.progress).setVisibility(View.GONE);
        view.findViewById(R.id.app).setVisibility(View.VISIBLE);

        ((TextView) view.findViewById(R.id.text1)).setText(app.getDisplayName());
        setText(R.id.text2, TextUtils.join(", ", line2));
        setText(R.id.text3, TextUtils.join(", ", line3));

        drawIcon((ImageView) view.findViewById(R.id.icon));
        redrawMoreButton();
    }

    private void drawIcon(ImageView imageView) {
        String tag = (String) imageView.getTag();
        if (!TextUtils.isEmpty(tag) && tag.equals(app.getPackageName())) {
            return;
        }
        imageView.setTag(app.getPackageName());
        LoadImageTask task = new LoadImageTask(imageView);
        LoadImageTask previousTask = tasks.get(imageView.hashCode());
        if (null != previousTask) {
            previousTask.cancel(true);
        }
        tasks.put(imageView.hashCode(), task);
        task.execute(app.getIconInfo());
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
        if (new ButtonCancel((YalpStoreActivity) view.getContext(), app).shouldBeVisible()) {
            enableCancelButton();
            new ListItemDownloadProgressUpdater(app.getPackageName(), AppBadge.this).execute(PurchaseTask.UPDATE_INTERVAL);
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
        } else {
            hideMoreButton();
        }
    }

    public void hideMoreButton() {
        if (null == view) {
            return;
        }
        view.findViewById(R.id.more).setVisibility(View.GONE);
        ((TextView) view.findViewById(R.id.more_progress)).setText("");
    }

    public void setProgress(int progress, int max) {
        if (null == view) {
            return;
        }
        view.findViewById(R.id.more).setVisibility(View.VISIBLE);
        enableCancelButton();
        ((TextView) view.findViewById(R.id.more_progress)).setText(((int) (((float) progress/max)*100)) + "%");
    }

    protected void enableMoreButton(int drawableResId, View.OnClickListener listener) {
        if (null == view) {
            return;
        }
        LinearLayout more = view.findViewById(R.id.more);
        more.setVisibility(View.VISIBLE);
        more.setOnClickListener(listener);
        ((ImageView) more.findViewById(R.id.more_image)).setImageResource(drawableResId);
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
                    view.getContext().startService(
                        new Intent(view.getContext().getApplicationContext(), CancelDownloadService.class)
                            .putExtra(CancelDownloadService.PACKAGE_NAME, app.getPackageName())
                    );
                    redrawMoreButton();
                }
            }
        );
    }
}
