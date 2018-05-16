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

import com.github.yeriomin.yalpstore.DownloadState;
import com.github.yeriomin.yalpstore.InstalledApkCopier;
import com.github.yeriomin.yalpstore.Paths;
import com.github.yeriomin.yalpstore.Util;
import com.github.yeriomin.yalpstore.model.App;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.zip.GZIPInputStream;

abstract public class Patcher {

    protected DownloadState downloadState;
    protected File patch;
    protected File originalApk;
    protected File destinationApk;

    public Patcher(Context context, App app) {
        Log.i(getClass().getSimpleName(), "Chosen delta patcher");
        downloadState = DownloadState.get(app.getPackageName());
        patch = Paths.getDeltaPath(context, app.getPackageName(), app.getVersionCode());
        originalApk = InstalledApkCopier.getCurrentApk(app);
        destinationApk = Paths.getApkPath(context, app.getPackageName(), app.getVersionCode());
    }

    abstract protected boolean patchSpecific() throws IOException;

    public boolean patch() {
        if (isGZipped(patch)) {
            File patchUncompressed = new File(patch.getAbsolutePath() + ".unpacked");
            Log.i(getClass().getSimpleName(), "Decompressing");
            File patchCompressed = patch;
            if (!GUnZip(patchCompressed, patchUncompressed)) {
                return false;
            }
            Log.i(getClass().getSimpleName(), "Deleting " + patchCompressed);
            patchCompressed.delete();
            patch = patchUncompressed;
        }
        Log.i(getClass().getSimpleName(), "Preparing to apply delta patch to " + downloadState.getApp().getPackageName());
        if (null == originalApk || !originalApk.exists()) {
            Log.e(getClass().getSimpleName(), "Could not find existing apk to patch it: " + originalApk);
            return false;
        }
        Log.i(getClass().getSimpleName(), "Patching with " + patch);
        try {
            boolean result = patchSpecific();
            if (result) {
                Log.i(getClass().getSimpleName(), "Patching successfully completed");
                downloadState.setApkChecksum(Util.getFileChecksum(destinationApk));
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

    static private boolean isGZipped(File f) {
        int magic = 0;
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(f, "r");
            magic = raf.read() & 0xff | ((raf.read() << 8) & 0xff00);
        } catch (IOException e) {
            Log.e(Patcher.class.getSimpleName(), "Could not check if patch is gzipped");
        } finally {
            Util.closeSilently(raf);
        }
        return magic == GZIPInputStream.GZIP_MAGIC;
    }

    static private boolean GUnZip(File from, File to) {
        GZIPInputStream zipInputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            zipInputStream = new GZIPInputStream(new FileInputStream(from));
            fileOutputStream = new FileOutputStream(to);
            byte[] buffer = new byte[0x1000];
            int count;
            while ((count = zipInputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, count);
            }
            return true;
        } catch (IOException e) {
            Log.e(Patcher.class.getSimpleName(), "Could not unzip the patch: " + e.getMessage());
            return false;
        } finally {
            Util.closeSilently(fileOutputStream);
            Util.closeSilently(zipInputStream);
        }
    }
}
