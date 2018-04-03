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

package com.github.yeriomin.yalpstore.task;

import android.app.Activity;
import android.os.Build;

import com.github.yeriomin.yalpstore.model.App;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ConvertToSystemTask extends SystemRemountTask {

    public ConvertToSystemTask(Activity activity, App app) {
        super(activity, app);
    }

    @Override
    protected List<String> getCommands() {
        List<String> commands = new ArrayList<>();
        String from = app.getPackageInfo().applicationInfo.sourceDir;
        String targetPath = getTargetPath();
        String targetDir = new File(targetPath).getParent();
        commands.add(getBusyboxCommand("mkdir " + targetDir));
        commands.add(getBusyboxCommand("chmod 755 " + targetDir));
        commands.add(getBusyboxCommand("mv " + from + " " + targetPath));
        commands.add(getBusyboxCommand("chmod 644 " + targetPath));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            commands.add(getBusyboxCommand("chown system " + targetPath));
            commands.add(getBusyboxCommand("chgrp system " + targetPath));
        }
        return commands;
    }

    private String getTargetPath() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return "/system/priv-app/" + app.getPackageName() + "/" + app.getPackageName() + ".apk";
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return "/system/priv-app/" + app.getPackageName() + ".apk";
        } else {
            return "/system/app/" + app.getPackageName() + ".apk";
        }
    }
}
