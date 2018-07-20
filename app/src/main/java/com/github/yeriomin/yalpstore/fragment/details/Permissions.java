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

import android.content.pm.PackageManager;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.view.View;
import android.widget.LinearLayout;

import com.github.yeriomin.yalpstore.PermissionsComparator;
import com.github.yeriomin.yalpstore.R;
import com.github.yeriomin.yalpstore.YalpStoreActivity;
import com.github.yeriomin.yalpstore.fragment.Abstract;
import com.github.yeriomin.yalpstore.model.App;
import com.github.yeriomin.yalpstore.widget.ExpansionPanel;
import com.github.yeriomin.yalpstore.widget.PermissionGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Permissions extends Abstract {

    private PackageManager pm;

    @Override
    public void draw() {
        activity.findViewById(R.id.permissions_panel).setVisibility(View.VISIBLE);
        activity.findViewById(R.id.permissions_panel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPermissionWidgets();
            }
        });
        if (!app.isInPlayStore()) {
            ((ExpansionPanel) activity.findViewById(R.id.permissions_panel)).toggle();
        }
    }

    public Permissions(YalpStoreActivity activity, App app) {
        super(activity, app);
        pm = activity.getPackageManager();
    }

    private void addPermissionWidgets() {
        PermissionsComparator comparator = new PermissionsComparator(activity);
        Set<String> newPermissions = new HashSet<>();
        if (!comparator.isSame(app)) {
            newPermissions.addAll(comparator.getNewPermissions());
        }
        Map<String, PermissionGroupInfo> groups = new HashMap<>();
        Map<String, Set<PermissionInfo>> permissions = new HashMap<>();
        for (String permissionName: app.getPermissions()) {
            PermissionInfo permissionInfo = getPermissionInfo(permissionName);
            if (null == permissionInfo) {
                continue;
            }
            PermissionGroupInfo permissionGroupInfo = getPermissionGroupInfo(permissionInfo);
            groups.put(permissionGroupInfo.name, permissionGroupInfo);
            if (!permissions.containsKey(permissionGroupInfo.name)) {
                permissions.put(permissionGroupInfo.name, new HashSet<PermissionInfo>());
            }
            permissions.get(permissionGroupInfo.name).add(permissionInfo);
        }
        LinearLayout container = activity.findViewById(R.id.permissions_container_widgets);
        container.removeAllViews();
        List<String> permissionGroupLabels = new ArrayList<>(groups.keySet());
        Collections.sort(permissionGroupLabels);
        for (String permissionGroupLabel: permissionGroupLabels) {
            PermissionGroupInfo groupInfo = groups.get(permissionGroupLabel);
            PermissionGroup widget = new PermissionGroup(activity);
            widget.setPermissionGroupInfo(groupInfo);
            widget.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            widget.setNewPermissions(newPermissions);
            widget.setPermissions(permissions.get(groupInfo.name));
            container.addView(widget);
        }
        activity.findViewById(R.id.permissions_none).setVisibility(permissionGroupLabels.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private PermissionInfo getPermissionInfo(String permissionName) {
        try {
            return pm.getPermissionInfo(permissionName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    private PermissionGroupInfo getPermissionGroupInfo(PermissionInfo permissionInfo) {
        PermissionGroupInfo permissionGroupInfo;
        if (null == permissionInfo.group) {
            permissionGroupInfo = getFakePermissionGroupInfo(permissionInfo.packageName);
        } else {
            try {
                permissionGroupInfo = pm.getPermissionGroupInfo(permissionInfo.group, 0);
            } catch (PackageManager.NameNotFoundException e) {
                permissionGroupInfo = getFakePermissionGroupInfo(permissionInfo.packageName);
            }
        }
        if (permissionGroupInfo.icon == 0) {
            permissionGroupInfo.icon = R.drawable.ic_permission_android;
        }
        return permissionGroupInfo;
    }

    private PermissionGroupInfo getFakePermissionGroupInfo(String packageName) {
        PermissionGroupInfo permissionGroupInfo = new PermissionGroupInfo();
        switch (packageName) {
            case "android":
                permissionGroupInfo.icon = R.drawable.ic_permission_android;
                permissionGroupInfo.name = "android";
                break;
            case "com.google.android.gsf":
            case "com.android.vending":
                permissionGroupInfo.icon = R.drawable.ic_permission_google;
                permissionGroupInfo.name = "google";
                break;
            default:
                permissionGroupInfo.icon = R.drawable.ic_permission_unknown;
                permissionGroupInfo.name = "unknown";
                break;
        }
        return permissionGroupInfo;

    }
}
