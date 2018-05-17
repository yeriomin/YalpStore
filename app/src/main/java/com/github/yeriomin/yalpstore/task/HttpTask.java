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

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import info.guardianproject.netcipher.NetCipher;
import info.guardianproject.netcipher.client.StrongConnectionBuilder;

abstract public class HttpTask extends AsyncTask<Void, Void, String> {

    private String url;
    protected int returnCode;
    protected String response;

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            HttpsURLConnection connection = NetCipher.getHttpsURLConnection(new URL(url), true);
            connection.setRequestMethod("GET");
            returnCode = connection.getResponseCode();
            response = StrongConnectionBuilder.slurp(connection.getInputStream());
        } catch (IOException e) {
            Log.e(getClass().getSimpleName(), "Could not get content from " + url + " : " + e.getMessage());
        }
        return response;
    }
}
