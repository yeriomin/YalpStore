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

package com.github.yeriomin.yalpstore.task.playstore;

import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import com.github.yeriomin.yalpstore.BaseActivity;
import com.github.yeriomin.yalpstore.BuildConfig;

import java.lang.ref.WeakReference;

public class SearchSuggestionTask extends AsyncTask<String, Void, Cursor> {

    private String requestString;
    private WeakReference<BaseActivity> activityRef;

    public SearchSuggestionTask(BaseActivity activity) {
        this.activityRef = new WeakReference<>(activity);
    }

    public String getRequestString() {
        return requestString;
    }

    @Override
    protected Cursor doInBackground(String... strings) {
        if (null == activityRef.get() || isCancelled()) {
            return null;
        }
        requestString = strings[0];
        return activityRef.get().getContentResolver().query(new Uri.Builder().scheme("content").authority(BuildConfig.APPLICATION_ID + ".YalpStoreSuggestionProvider").appendEncodedPath(requestString).build(), null, null, null, null);
    }

    @Override
    protected void onPostExecute(Cursor cursor) {
        if (null == activityRef.get() || isCancelled()) {
            cursor.close();
            return;
        }
        activityRef.get().showSuggestions(cursor);
    }
}
