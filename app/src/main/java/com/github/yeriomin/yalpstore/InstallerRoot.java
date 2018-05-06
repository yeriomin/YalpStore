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
import android.util.Log;

import com.github.yeriomin.yalpstore.model.App;

import java.util.List;

import eu.chainfire.libsuperuser.Shell;

public class InstallerRoot extends InstallerBackground {

    public InstallerRoot(Context context) {
        super(context);
    }

    @Override
    protected void install(App app) {
        InstallationState.setInstalling(app.getPackageName());
        boolean success = shellInstall(Paths.getApkPath(context, app.getPackageName(), app.getVersionCode()).toString());
        if (success) {
            InstallationState.setSuccess(app.getPackageName());
        } else {
            InstallationState.setFailure(app.getPackageName());
        }
        sendBroadcast(app.getPackageName(), true);
        postInstallationResult(app, success);
    }

    private boolean shellInstall(String file) {
        List<String> lines = Shell.SU.run("pm install -i \"" + BuildConfig.APPLICATION_ID + "\" -r " + file);
        if (null != lines) {
            for (String line: lines) {
                Log.i(getClass().getSimpleName(), line);
            }
        }
        return null != lines && lines.size() == 1 && lines.get(0).equals("Success");
    }
}
