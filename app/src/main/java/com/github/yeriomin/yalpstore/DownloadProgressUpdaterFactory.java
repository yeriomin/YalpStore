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

import android.widget.ProgressBar;

import com.github.yeriomin.yalpstore.view.AppBadge;

public class DownloadProgressUpdaterFactory {

    static public DownloadProgressUpdater get(YalpStoreActivity activity, String packageName) {
        if (activity instanceof DetailsActivity) {
            return get((DetailsActivity) activity, packageName);
        } else if (activity instanceof AppListActivity) {
            return get((AppListActivity) activity, packageName);
        }
        return null;
    }

    static private DownloadProgressUpdater get(DetailsActivity activity, String packageName) {
        return new DownloadProgressBarUpdater(packageName, (ProgressBar) activity.findViewById(R.id.download_progress));
    }

    static private DownloadProgressUpdater get(AppListActivity activity, String packageName) {
        AppBadge appBadge = (AppBadge) activity.getListItem(packageName);
        if (null == appBadge) {
            return null;
        }
        return new ListItemDownloadProgressUpdater(packageName, appBadge);
    }
}
