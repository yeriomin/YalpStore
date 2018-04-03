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
import android.os.AsyncTask;

import java.io.File;
import java.lang.ref.WeakReference;

abstract public class CleanupTask extends AsyncTask<Void, Void, Void> {

    protected WeakReference<Context> contextRef = new WeakReference<>(null);

    public CleanupTask(Context context) {
        this.contextRef = new WeakReference<>(context);
    }

    abstract protected boolean shouldDelete(File file);
    abstract protected File[] getFiles();

    @Override
    protected Void doInBackground(Void... voids) {
        if (null == contextRef.get()) {
            return null;
        }
        for (File file: getFiles()) {
            if (shouldDelete(file)) {
                file.delete();
            }
        }
        return null;
    }
}
