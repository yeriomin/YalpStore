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

import android.os.Build;
import android.view.View;
import android.widget.TextView;

import com.github.yeriomin.yalpstore.PreferenceUtil;
import com.github.yeriomin.yalpstore.R;
import com.github.yeriomin.yalpstore.YalpStoreActivity;
import com.github.yeriomin.yalpstore.fragment.Abstract;
import com.github.yeriomin.yalpstore.model.App;
import com.github.yeriomin.yalpstore.task.ExodusSearchTask;
import com.github.yeriomin.yalpstore.view.HttpTaskOnClickListener;

public class Exodus extends Abstract {

    @Override
    public void draw() {
        TextView view = activity.findViewById(R.id.exodus);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP || null == view) {
            // TLS...
            return;
        }
        if (!app.isInPlayStore()) {
            view.setVisibility(View.GONE);
            return;
        }
        view.setVisibility(View.VISIBLE);
        HttpTaskOnClickListener listener = new HttpTaskOnClickListener(new ExodusSearchTask(view, app.getPackageName()));
        if (PreferenceUtil.getBoolean(activity, PreferenceUtil.PREFERENCE_EXODUS)) {
            listener.onClick(view);
        } else {
            view.setText(activity.getString(R.string.details_exodus, activity.getString(R.string.details_exodus_tap_to_check)));
            view.setOnClickListener(listener);
        }
    }

    public Exodus(YalpStoreActivity activity, App app) {
        super(activity, app);
    }
}
