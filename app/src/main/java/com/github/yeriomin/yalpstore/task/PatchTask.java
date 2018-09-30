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

import android.util.Log;

import com.github.yeriomin.yalpstore.Paths;
import com.github.yeriomin.yalpstore.delta.PatcherFactory;
import com.github.yeriomin.yalpstore.download.DownloadManager;
import com.github.yeriomin.yalpstore.download.Request;
import com.github.yeriomin.yalpstore.download.RequestDelta;
import com.github.yeriomin.yalpstore.model.App;

import java.io.IOException;
import java.util.jar.JarFile;

public class PatchTask extends TaskWithProgress<Boolean> {

    private App app;
    private RequestDelta request;

    public void setApp(App app) {
        this.app = app;
    }

    public void setRequest(RequestDelta request) {
        this.request = request;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        DownloadManager dm = new DownloadManager(context);
        if (result) {
            dm.complete(request.getPackageName(), Request.Type.DELTA.name());
        } else {
            dm.error(request.getPackageName(), DownloadManager.Error.DELTA_FAILED);
        }
    }

    @Override
    protected Boolean doInBackground(String... strings) {
        if (!PatcherFactory.get(context, app, request.getPatchFormat()).patch()) {
            Log.e(getClass().getSimpleName(), "Delta patching failed for " + request.getPackageName());
            return false;
        }
        try {
            new JarFile(Paths.getApkPath(context, app.getPackageName(), app.getVersionCode())).entries();
        } catch (SecurityException e) {
            // No rights to open the file we just created? Not a delta issue.
        } catch (IOException e) {
            Log.e(getClass().getSimpleName(), "Delta patched apk for " + request.getPackageName() + " could not be opened as a jar file: " + e.getMessage());
            return false;
        }
        return true;
    }
}
