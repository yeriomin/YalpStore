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

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.view.View;

import com.github.yeriomin.yalpstore.ContextUtil;
import com.github.yeriomin.yalpstore.DetailsActivity;
import com.github.yeriomin.yalpstore.R;
import com.github.yeriomin.yalpstore.model.App;
import com.github.yeriomin.yalpstore.view.UriOnClickListener;

public class Video extends Abstract {

    public Video(DetailsActivity activity, App app) {
        super(activity, app);
    }

    @Override
    public void draw() {
        if (TextUtils.isEmpty(app.getVideoUrl())) {
            return;
        }
        prepareLink(activity.findViewById(R.id.video));
    }

    private void prepareLink(View linkView) {
        linkView.setVisibility(View.VISIBLE);
        linkView.setOnClickListener(new UriOnClickListener(activity, app.getVideoUrl()) {
            @Override
            protected void onActivityNotFound(ActivityNotFoundException e) {
                super.onActivityNotFound(e);
                ((ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE)).setText(app.getVideoUrl());
                ContextUtil.toast(context.getApplicationContext(), R.string.about_copied_to_clipboard);
            }
        });
    }
}
