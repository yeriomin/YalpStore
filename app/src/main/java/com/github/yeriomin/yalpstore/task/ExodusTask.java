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
import android.view.View;
import android.widget.TextView;

import com.github.yeriomin.yalpstore.R;

import java.lang.ref.WeakReference;

abstract public class ExodusTask extends HttpTask {

    static protected final String BASE_URL = "https://reports.exodus-privacy.eu.org";
    static protected final String LINK_WEB_FORM = BASE_URL + "/analysis/submit/";
    static protected final String LINK_WEB_REPORT = BASE_URL + "/reports/";

    protected WeakReference<TextView> viewRef;
    protected String packageName;

    public ExodusTask(String url, String method, TextView view, String packageName) {
        super(url, method);
        this.viewRef = new WeakReference<>(view);
        this.packageName = packageName;
    }

    protected void updateTextView(View.OnClickListener listener, int resId, Object... formatArgs) {
        Context c = viewRef.get().getContext();
        viewRef.get().setText(c.getString(R.string.details_exodus, c.getString(resId, formatArgs)));
        if (null != listener) {
            viewRef.get().setOnClickListener(listener);
        }
    }
}
