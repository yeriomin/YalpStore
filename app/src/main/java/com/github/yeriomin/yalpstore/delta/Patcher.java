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

package com.github.yeriomin.yalpstore.delta;

import android.content.Context;
import android.util.Log;

import com.github.yeriomin.yalpstore.InstalledApkCopier;
import com.github.yeriomin.yalpstore.Paths;
import com.github.yeriomin.yalpstore.model.App;

import java.io.File;
import java.io.IOException;

abstract public class Patcher {

    protected File patch;
    protected File originalApk;
    protected File destinationApk;

    public Patcher(Context context, App app) {
        Log.i(getClass().getSimpleName(), "Chosen delta patcher");
        patch = Paths.getDeltaPath(context, app.getPackageName(), app.getVersionCode());
        originalApk = InstalledApkCopier.getCurrentApk(app);
        destinationApk = Paths.getApkPath(context, app.getPackageName(), app.getVersionCode());
    }

    abstract protected boolean patchSpecific() throws IOException;

    public boolean patch() {
        Log.i(getClass().getSimpleName(), "Preparing to apply delta patch to " + originalApk);
        if (null == originalApk || !originalApk.exists()) {
            Log.e(getClass().getSimpleName(), "Could not find existing apk to patch it: " + originalApk);
            return false;
        }
        Log.i(getClass().getSimpleName(), "Patching with " + patch);
        try {
            boolean result = patchSpecific();
            if (result) {
                Log.i(getClass().getSimpleName(), "Patching successfully completed");
            }
            return result;
        } catch (IOException e) {
            Log.e(getClass().getSimpleName(), "Patching failed: " + e.getClass().getName() + " " + e.getMessage());
            return false;
        } finally {
            Log.i(getClass().getSimpleName(), "Deleting " + patch);
            patch.delete();
        }
    }
}
