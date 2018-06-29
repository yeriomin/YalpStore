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

import android.text.TextUtils;
import android.widget.TextView;

import com.github.yeriomin.yalpstore.R;
import com.github.yeriomin.yalpstore.view.UriOnClickListener;

public class ExodusSubmitTask extends ExodusTask {

    static private final String REDIRECT_LOCATION_PREFIX = "/analysis/";
    static private final String LINK_ANALYSIS = BASE_URL + REDIRECT_LOCATION_PREFIX;

    private String cookie;
    private String middlewareToken;
    private int reportId;

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public void setMiddlewareToken(String middlewareToken) {
        this.middlewareToken = middlewareToken;
    }

    public ExodusSubmitTask(TextView view, String packageName) {
        super(LINK_WEB_FORM, "POST", view, packageName);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        updateTextView(null, R.string.details_exodus_submitting);
    }

    @Override
    protected String doInBackground(String... strings) {
        connection.setInstanceFollowRedirects(false);
        addHeader("Referer", LINK_WEB_FORM);
        addHeader("Cookie", ExodusCsrfTask.COOKIE_NAME + "=" + cookie);
        addFormField(ExodusCsrfTask.FORM_FIELD_NAME, middlewareToken);
        addFormField("handle", packageName);
        String result = super.doInBackground(strings);
        String location = connection.getHeaderField("location");
        if (returnCode == 302 && !TextUtils.isEmpty(location) && location.startsWith(REDIRECT_LOCATION_PREFIX)) {
            reportId = Integer.parseInt(location.substring(REDIRECT_LOCATION_PREFIX.length()));
        }
        return result;
    }

    @Override
    protected void onPostExecute(String s) {
        if (null == viewRef.get()) {
            return;
        }
        super.onPostExecute(s);
        if (reportId > 0) {
            UriOnClickListener listener = new UriOnClickListener(viewRef.get().getContext(), LINK_ANALYSIS + reportId);
            updateTextView(listener, R.string.details_exodus_view);
            listener.onClick(viewRef.get());
        } else {
            updateTextView(new UriOnClickListener(viewRef.get().getContext(), LINK_WEB_FORM), R.string.submit);
        }
    }
}
