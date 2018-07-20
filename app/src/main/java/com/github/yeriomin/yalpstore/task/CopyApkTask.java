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

import com.github.yeriomin.yalpstore.ContextUtil;
import com.github.yeriomin.yalpstore.InstalledApkCopier;
import com.github.yeriomin.yalpstore.R;
import com.github.yeriomin.yalpstore.model.App;

import java.lang.ref.WeakReference;

public class CopyApkTask extends AsyncTask<App, Void, Boolean> {

    private WeakReference<Context> contextRef;

    public CopyApkTask(Context context) {
        contextRef = new WeakReference<>(context);
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (null == contextRef.get()) {
            return;
        }
        ContextUtil.toastLong(
            contextRef.get().getApplicationContext(),
            contextRef.get().getString(result
                ? R.string.details_saved_in_downloads
                : R.string.details_could_not_copy_apk
            )
        );
    }

    @Override
    protected Boolean doInBackground(App... apps) {
        if (null == contextRef.get()) {
            return false;
        }
        return new InstalledApkCopier(contextRef.get()).copy(apps[0]);
    }
}
