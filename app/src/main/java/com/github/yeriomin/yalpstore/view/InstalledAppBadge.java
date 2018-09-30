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

import android.content.Context;
import android.view.View;

import com.github.yeriomin.yalpstore.BlackWhiteListManager;
import com.github.yeriomin.yalpstore.R;
import com.github.yeriomin.yalpstore.download.DownloadManager;

public class InstalledAppBadge extends AppBadge {

    @Override
    public void draw() {
        line2.clear();
        line3.clear();
        Context c = view.getContext();
        BlackWhiteListManager manager = new BlackWhiteListManager(c);
        if (manager.contains(app.getPackageName())) {
            line2.add(c.getString(manager.isBlack() ? R.string.list_app_blacklisted : R.string.list_app_whitelisted));
        }
        if (app.isSystem()) {
            line3.add(c.getString(R.string.list_app_system));
        }
        super.draw();
    }

    @Override
    public void redrawMoreButton() {
        if (DownloadManager.isRunning(app.getPackageName())) {
            enableCancelButton();
        } else {
            enableMoreButton();
        }
    }

    private void enableMoreButton() {
        enableMoreButton(
            R.drawable.ic_more_vert,
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    view.performLongClick();
                }
            }
        );
    }
}
