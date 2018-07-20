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

package com.github.yeriomin.yalpstore.fragment;

import android.content.Intent;
import android.net.Uri;
import android.view.View;

import com.github.yeriomin.yalpstore.R;
import com.github.yeriomin.yalpstore.YalpStoreActivity;
import com.github.yeriomin.yalpstore.model.App;
import com.github.yeriomin.yalpstore.task.CheckShellTask;
import com.github.yeriomin.yalpstore.task.UninstallSystemAppTask;

public class ButtonUninstall extends Button {

    public ButtonUninstall(YalpStoreActivity activity, App app) {
        super(activity, app);
    }

    @Override
    protected View getButton() {
        return activity.findViewById(R.id.uninstall);
    }

    @Override
    public boolean shouldBeVisible() {
        return isInstalled();
    }

    @Override
    protected void onButtonClick(View v) {
        uninstall();
    }

    public void uninstall() {
        if (isSystemAndReadyForPermanentUninstallation()) {
            askAndUninstall();
        } else {
            activity.startActivity(new Intent(Intent.ACTION_DELETE, Uri.parse("package:" + app.getPackageName())));
        }
    }

    private boolean isSystemAndReadyForPermanentUninstallation() {
        return app.isSystem()
            && null != app.getPackageInfo().applicationInfo
            && null != app.getPackageInfo().applicationInfo.sourceDir
            && app.getPackageInfo().applicationInfo.sourceDir.startsWith("/system/")
        ;
    }

    private void askAndUninstall() {
        CheckShellTask checkShellTask = new CheckShellTask(activity);
        checkShellTask.setPrimaryTask(new UninstallSystemAppTask(activity, app));
        checkShellTask.execute();
    }
}
