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

import android.util.Log;

import com.github.yeriomin.yalpstore.NetworkUtil;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import info.guardianproject.netcipher.client.StrongConnectionBuilder;

abstract public class HttpTask extends LowCpuIntensityTask<String, Void, String> {

    protected HttpsURLConnection connection;
    private final Map<String, String> headers = new HashMap<>();
    private final Map<String, String> formFields = new HashMap<>();
    protected int returnCode;
    protected String response;

    public HttpTask(String url, String method) {
        try {
            connection = (HttpsURLConnection) NetworkUtil.getHttpURLConnection(url);
            connection.setRequestMethod(method);
        } catch (IOException e) {
            Log.e(getClass().getSimpleName(), "Could not get content from " + url + " : " + e.getMessage());
        }
    }

    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    public void addFormField(String name, String value) {
        formFields.put(name, value);
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            for (String name: headers.keySet()) {
                connection.addRequestProperty(name, headers.get(name));
            }
            if (!formFields.isEmpty()) {
                addFromData();
            }
            returnCode = connection.getResponseCode();
            processResponseBody(connection.getInputStream());
        } catch (IOException e) {
            try {
                response = StrongConnectionBuilder.slurp(connection.getErrorStream());
            } catch (IOException e1) {
                // If reading the error message fails, there is nothing else to do
            }
            Log.e(getClass().getSimpleName(), "Could not get content from " + connection.getURL() + " : " + e.getClass().getCanonicalName());
        }
        return response;
    }

    protected void processResponseBody(InputStream is) throws IOException {
        response = StrongConnectionBuilder.slurp(is);
    }

    private void addFromData() throws IOException {
        String boundary = "----FormBoundary" + Long.toString(System.currentTimeMillis()) + "----";
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        connection.setDoOutput(true);
        DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
        outputStream.writeBytes(getBodyString(boundary));
        outputStream.flush();
        outputStream.close();
    }

    private String getBodyString(String boundary) {
        StringBuilder stringBuilder = new StringBuilder();
        String lineEnd = "\r\n";
        for (String key: formFields.keySet()) {
            stringBuilder.append("--").append(boundary).append(lineEnd);
            stringBuilder.append("Content-Disposition: form-data; name=\"").append(key).append("\"").append(lineEnd);
            stringBuilder.append(lineEnd);
            stringBuilder.append(formFields.get(key));
            stringBuilder.append(lineEnd);
        }
        stringBuilder.append("--").append(boundary).append("--").append(lineEnd).append(lineEnd);
        return stringBuilder.toString();
    }
}
