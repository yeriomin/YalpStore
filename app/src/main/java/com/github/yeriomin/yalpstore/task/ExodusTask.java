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
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.github.yeriomin.yalpstore.R;
import com.github.yeriomin.yalpstore.view.UriOnClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ExodusTask extends HttpTask {

    static private final String LINK_API = "https://reports.exodus-privacy.eu.org/api/search/";
    static private final String LINK_WEB_REPORT = "https://reports.exodus-privacy.eu.org/reports/";

    private TextView view;
    private String packageName;
    private int trackers;
    private int reportId;

    public ExodusTask(TextView view, String packageName) {
        this.view = view;
        this.packageName = packageName;
        setUrl(LINK_API + packageName);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        setText(R.string.details_exodus_searching);
    }

    @Override
    protected String doInBackground(Void... voids) {
        String result = super.doInBackground(voids);
        if (TextUtils.isEmpty(result)) {
            return result;
        }
        try {
            Log.i(getClass().getSimpleName(), "result=" + result);
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
        super.onPostExecute(s);
        if (reportId > 0) {
            setText(R.string.details_exodus_found, trackers);
            view.setOnClickListener(new UriOnClickListener(view.getContext(), LINK_WEB_REPORT + reportId + "/"));
        } else {
            setText(R.string.details_exodus_not_found);
        }
    }

    private void setText(int resId, Object... formatArgs) {
        Context c = view.getContext();
        view.setText(c.getString(R.string.details_exodus, c.getString(resId, formatArgs)));
    }
}
