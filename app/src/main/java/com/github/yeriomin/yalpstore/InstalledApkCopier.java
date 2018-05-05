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
import android.preference.PreferenceManager;
import android.util.Log;

import com.github.yeriomin.yalpstore.model.App;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class InstalledApkCopier {

    private File destinationDir;

    public InstalledApkCopier(Context context) {
        destinationDir = new File(
            Paths.getStorageRoot(context),
            PreferenceManager.getDefaultSharedPreferences(context).getString(PreferenceUtil.PREFERENCE_DOWNLOAD_DIRECTORY, "")
        );
    }

    public boolean copy(App app) {
        File destination = getDestination(app);
        if (destination.exists()) {
            Log.i(InstalledApkCopier.class.getSimpleName(), destination.toString() + " exists");
            return true;
        }
        File currentApk = getCurrentApk(app);
        if (null == currentApk) {
            Log.e(InstalledApkCopier.class.getSimpleName(), "applicationInfo.sourceDir is empty");
            return false;
        }
        if (!currentApk.exists()) {
            Log.e(InstalledApkCopier.class.getSimpleName(), currentApk + " does not exist");
            return false;
        }
        return copy(currentApk, destination);
    }

    static public File getCurrentApk(App app) {
        if (null != app.getPackageInfo() && null != app.getPackageInfo().applicationInfo) {
            return new File(app.getPackageInfo().applicationInfo.sourceDir);
        }
        return null;
    }

    static private boolean copy(File input, File output) {
        InputStream in = null;
        OutputStream out = null;
        File dir = output.getParentFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }
        try {
            in = new FileInputStream(input);
            out = new FileOutputStream(output);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }

            out.flush();
            return true;
        } catch (IOException e) {
            Log.e(InstalledApkCopier.class.getSimpleName(), e.getClass().getName() + " " + e.getMessage());
            return false;
        } finally {
            Util.closeSilently(in);
            Util.closeSilently(out);
        }
    }

    private File getDestination(App app) {
        return new File(
            destinationDir,
            app.getPackageName() + "." + String.valueOf(app.getInstalledVersionCode()) + ".apk"
        );
    }
}
