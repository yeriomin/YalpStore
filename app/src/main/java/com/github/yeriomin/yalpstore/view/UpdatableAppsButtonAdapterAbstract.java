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

import android.view.View;

import com.github.yeriomin.yalpstore.UpdatableAppsActivity;
import com.github.yeriomin.yalpstore.YalpStoreApplication;
import com.github.yeriomin.yalpstore.YalpStorePermissionManager;

public class UpdatableAppsButtonAdapterAbstract extends ButtonAdapter {

    public UpdatableAppsButtonAdapterAbstract(View button) {
        super(button);
    }

    public UpdatableAppsButtonAdapterAbstract init(final UpdatableAppsActivity activity) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YalpStorePermissionManager permissionManager = new YalpStorePermissionManager(activity);
                if (permissionManager.checkPermission()) {
                    activity.launchUpdateAll();
                } else {
                    permissionManager.requestPermission(UpdatableAppsActivity.REQUEST_CODE_UPDATE_ALL);
                }
            }
        });
        if (((YalpStoreApplication) activity.getApplication()).isBackgroundUpdating()) {
            setUpdating();
        } else if (!activity.getListedPackageNames().isEmpty()) {
            setReady();
        }
        return this;
    }

    public UpdatableAppsButtonAdapterAbstract setReady() {
        show();
        enable();
        return this;
    }

    public UpdatableAppsButtonAdapterAbstract setUpdating() {
        show();
        disable();
        return this;
    }
}
