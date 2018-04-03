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

import com.github.yeriomin.yalpstore.view.AppBadge;

public class AppListDownloadReceiver extends ForegroundDownloadReceiver {

    public AppListDownloadReceiver(AppListActivity activity) {
        super(activity);
    }

    @Override
    protected void cleanup() {
        draw();
    }

    @Override
    protected void draw() {
        AppBadge appBadge = getAppBadge();
        if (null != appBadge) {
            appBadge.redrawMoreButton();
        }
    }

    @Override
    protected void process(Context context, Intent intent) {
        AppListActivity activity = (AppListActivity) activityRef.get();
        if (!activity.getListedPackageNames().contains(state.getApp().getPackageName())) {
            return;
        }
        super.process(context, intent);
    }

    private AppBadge getAppBadge() {
        if (null == activityRef.get() || null == state || null == state.getApp()) {
            return null;
        }
        return (AppBadge) ((AppListActivity) activityRef.get()).getListItem(state.getApp().getPackageName());
    }
}
