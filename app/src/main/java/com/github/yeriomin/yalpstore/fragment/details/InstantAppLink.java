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

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.github.yeriomin.yalpstore.R;
import com.github.yeriomin.yalpstore.YalpStoreActivity;
import com.github.yeriomin.yalpstore.fragment.Abstract;
import com.github.yeriomin.yalpstore.model.App;
import com.github.yeriomin.yalpstore.view.UriOnClickListener;

public class InstantAppLink extends Abstract {

    @Override
    public void draw() {
        TextView instantAppView = activity.findViewById(R.id.instant_app);
        if (null == instantAppView) {
            return;
        }
        if (TextUtils.isEmpty(app.getInstantAppLink())) {
            instantAppView.setVisibility(View.GONE);
            return;
        }
        instantAppView.setVisibility(View.VISIBLE);
        instantAppView.setOnClickListener(new UriOnClickListener(activity, app.getInstantAppLink()));
    }

    public InstantAppLink(YalpStoreActivity activity, App app) {
        super(activity, app);
    }
}
