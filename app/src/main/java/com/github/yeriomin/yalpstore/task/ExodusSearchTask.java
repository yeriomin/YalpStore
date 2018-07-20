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
import android.util.Log;
import android.widget.TextView;

import com.github.yeriomin.yalpstore.R;
import com.github.yeriomin.yalpstore.view.HttpTaskOnClickListener;
import com.github.yeriomin.yalpstore.view.UriOnClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ExodusSearchTask extends ExodusTask {

    static private final String LINK_API = BASE_URL + "/api/search/";

    private int trackers;
    private int reportId;

    public ExodusSearchTask(TextView view, String packageName) {
        super(LINK_API + packageName, "GET", view, packageName);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        updateTextView(null, R.string.details_exodus_searching);
    }

    @Override
    protected String doInBackground(String... strings) {
        if (null == viewRef.get()) {
            return null;
        }
        addHeader("Content-Type", "application/json");
        addHeader("Accept", "application/json");
        addHeader("Authorization", "Token " + viewRef.get().getContext().getString(R.string.exodus_api_key));
        String result = super.doInBackground(strings);
        if (TextUtils.isEmpty(result)) {
            return result;
        }
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (!jsonObject.has(packageName)) {
                return result;
            }
            JSONObject response = jsonObject.getJSONObject(packageName);
            if (!response.has("reports") || response.getJSONArray("reports").length() == 0) {
                return result;
            }
            JSONArray reports = response.getJSONArray("reports");
            JSONObject latestReport = null;
            for (int i = 0; i < reports.length(); i++) {
                JSONObject report = reports.getJSONObject(i);
                if (null == latestReport || latestReport.getInt("version_code") < report.getInt("version_code")) {
                    latestReport = report;
                }
            }
            trackers = latestReport.getJSONArray("trackers").length();
            reportId = latestReport.getInt("id");
        } catch (JSONException e) {
            Log.e(getClass().getSimpleName(), "Could not parse JSON for " + packageName + " : " + e.getMessage());
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
            updateTextView(new UriOnClickListener(viewRef.get().getContext(), LINK_WEB_REPORT + reportId + "/"), R.string.details_exodus_found, trackers);
        } else {
            updateTextView(
                new HttpTaskOnClickListener(new ExodusCsrfTask(viewRef.get(), packageName)),
                R.string.details_exodus_view
            );
        }
    }
}
