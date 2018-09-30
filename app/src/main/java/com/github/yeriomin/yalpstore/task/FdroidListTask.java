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
import android.os.Build;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;

import com.github.yeriomin.yalpstore.NetworkUtil;
import com.github.yeriomin.yalpstore.Util;
import com.github.yeriomin.yalpstore.YalpStoreApplication;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;


public class FdroidListTask extends LowCpuIntensityTask<Void, Void, Void> {

    static private final String FDROID_REPO_URL = "https://f-droid.org/repo/index.xml";
    static private final String FDROID_REPO_LOCAL_XML = "fdroid.xml";

    private File localXmlFile;

    public FdroidListTask(Context context) {
        localXmlFile = new File(context.getCacheDir(), FDROID_REPO_LOCAL_XML);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        if (localXmlFile.exists() && localXmlFile.lastModified() + DateUtils.WEEK_IN_MILLIS < System.currentTimeMillis()) {
            localXmlFile.delete();
        }
        if (!localXmlFile.exists()) {
            downloadXml();
        }
        parseXml();
        Log.i(getClass().getSimpleName(), "F-Droid app list size: " + YalpStoreApplication.fdroidPackageNames.size());
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            localXmlFile.delete();
        }
        return null;
    }

    private void downloadXml() {
        try {
            URL url = new URL(FDROID_REPO_URL);
            NetworkUtil.getHttpURLConnection(url).connect();
            InputStream input = new BufferedInputStream(url.openStream(), 8192);
            OutputStream output = new FileOutputStream(localXmlFile);
            byte data[] = new byte[1024];
            int count;
            while ((count = input.read(data)) != -1) {
                output.write(data, 0, count);
            }
            output.flush();
            Util.closeSilently(output);
            Util.closeSilently(input);
        } catch (IOException e) {
            Log.e(getClass().getSimpleName(), "Could not download and save F-Droid repo file to cache: " + e.getMessage());
        }
    }

    private void parseXml() {
        try {
            SAXParserFactory.newInstance().newSAXParser().parse(new FileInputStream(localXmlFile), new SaxHandler());
        } catch (IOException | ParserConfigurationException | SAXException e) {
            Log.e(getClass().getSimpleName(), "Could not read or parse F-Droid repo file: " + e.getMessage());
        }
    }

    private static class SaxHandler extends DefaultHandler {

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            super.startElement(uri, localName, qName, attributes);
            String packageName = attributes.getValue("id");
            if (qName.equalsIgnoreCase("application") && !TextUtils.isEmpty(packageName)) {
                YalpStoreApplication.fdroidPackageNames.add(packageName);
            }
        }
    }
}
