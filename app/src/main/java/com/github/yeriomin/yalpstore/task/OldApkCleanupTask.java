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

import android.content.Context;

import java.io.File;

public class OldApkCleanupTask extends CleanupTask {

    static public final long VALID_MILLIS = 1000*60*60*24;

    private boolean deleteAll;

    public void setDeleteAll(boolean deleteAll) {
        this.deleteAll = deleteAll;
    }

    public OldApkCleanupTask(Context context) {
        super(context);
    }

    @Override
    protected boolean shouldDelete(File file) {
        return file.getName().endsWith(".apk")
            && (deleteAll || file.lastModified() + VALID_MILLIS < System.currentTimeMillis())
        ;
    }

    @Override
    protected File[] getFiles() {
        return contextRef.get().getFilesDir().listFiles();
    }
}
