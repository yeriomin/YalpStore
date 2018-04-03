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

package com.github.yeriomin.yalpstore.widget;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.yeriomin.yalpstore.R;
import com.github.yeriomin.yalpstore.view.DialogWrapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PermissionGroup extends LinearLayout {

    private PermissionGroupInfo permissionGroupInfo;
    private Map<String, String> permissionMap = new HashMap<>();
    private PackageManager pm;

    public PermissionGroup(Context context) {
        super(context);
        init();
    }

    public PermissionGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PermissionGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PermissionGroup(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void setPermissionGroupInfo(final PermissionGroupInfo permissionGroupInfo) {
        this.permissionGroupInfo = permissionGroupInfo;
        ((ImageView) findViewById(R.id.permission_group_icon)).setImageDrawable(getPermissionGroupIcon(permissionGroupInfo));
    }

    public void addPermission(PermissionInfo permissionInfo) {
        CharSequence label = permissionInfo.loadLabel(pm);
        CharSequence description = permissionInfo.loadDescription(pm);
        permissionMap.put(TextUtils.isEmpty(label) ? "" : label.toString(), TextUtils.isEmpty(description) ? "" : description.toString());
        List<String> permissionLabels = new ArrayList<>(permissionMap.keySet());
        Collections.sort(permissionLabels);
        LinearLayout permissionLabelsView = findViewById(R.id.permission_labels);
        permissionLabelsView.removeAllViews();
        for (String permissionLabel: permissionLabels) {
            addPermissionLabel(permissionLabelsView, permissionLabel, permissionMap.get(permissionLabel));
        }
    }

    private void init() {
        inflate(getContext(), R.layout.permission_group_widget_layout, this);
        pm = getContext().getPackageManager();
    }

    private void addPermissionLabel(LinearLayout permissionLabelsView, String label, String description) {
        TextView newView = new TextView(getContext());
        newView.setText(label);
        newView.setOnClickListener(getOnClickListener(description));
        permissionLabelsView.addView(newView);
    }

    private Drawable getPermissionGroupIcon(PermissionGroupInfo permissionGroupInfo) {
        try {
            return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1
                ? getContext().getResources().getDrawable(permissionGroupInfo.icon, getContext().getTheme())
                : getContext().getResources().getDrawable(permissionGroupInfo.icon)
            ;
        } catch (Resources.NotFoundException e) {
            return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1
                ? permissionGroupInfo.loadUnbadgedIcon(pm)
                : permissionGroupInfo.loadIcon(pm)
            ;
        }
    }

    private OnClickListener getOnClickListener(final String message) {
        if (TextUtils.isEmpty(message)) {
            return null;
        }
        CharSequence label = null == permissionGroupInfo ? "" : permissionGroupInfo.loadLabel(pm);
        final String title = TextUtils.isEmpty(label) ? "" : label.toString();
        return new OnClickListener() {
            @Override
            public void onClick(View v) {
                new DialogWrapper((Activity) getContext())
                    .setIcon(getPermissionGroupIcon(permissionGroupInfo))
                    .setTitle((title.equals(permissionGroupInfo.name) || title.equals(permissionGroupInfo.packageName)) ? "" : title)
                    .setMessage(message)
                    .show()
                ;
            }
        };
    }
}
