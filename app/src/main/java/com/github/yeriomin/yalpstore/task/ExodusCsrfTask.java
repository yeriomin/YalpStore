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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class ExodusCsrfTask extends ExodusTask {

    static public final String COOKIE_NAME = "csrftoken";
    static public final String FORM_FIELD_NAME = "csrfmiddlewaretoken";

    private String middlewareToken;
    private String cookie;

    public ExodusCsrfTask(TextView view, String packageName) {
        super(LINK_WEB_FORM, "GET", view, packageName);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        updateTextView(null, R.string.details_exodus_submitting);
    }

    @Override
    protected String doInBackground(String... strings) {
        String result = super.doInBackground(strings);
        cookie = getCookieValue(connection.getHeaderField("Set-Cookie"));
        middlewareToken = getMiddlewareToken(result);
        return result;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (!TextUtils.isEmpty(cookie) && !TextUtils.isEmpty(middlewareToken)) {
            ExodusSubmitTask task = new ExodusSubmitTask(viewRef.get(), packageName);
            task.setCookie(cookie);
            task.setMiddlewareToken(middlewareToken);
            task.execute();
        } else {
            updateTextView(new UriOnClickListener(viewRef.get().getContext(), LINK_WEB_FORM), R.string.submit);
        }
    }

    private String getMiddlewareToken(String page) {
        Matcher matcher = Pattern.compile("<input type='hidden' name='" + FORM_FIELD_NAME + "' value='(.*?)' />").matcher(page);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }

    private String getCookieValue(String rawCookie) {
        if (TextUtils.isEmpty(rawCookie)) {
            return "";
        }
        String[] parts = TextUtils.split(rawCookie, ";");
        if (parts.length == 0 || !parts[0].startsWith(COOKIE_NAME)) {
            return "";
        }
        parts = TextUtils.split(parts[0], "=");
        if (parts.length < 2) {
            return "";
        }
        return parts[1];
    }
}
