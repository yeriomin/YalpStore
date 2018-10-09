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

package com.github.yeriomin.yalpstore.bugreport;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.github.yeriomin.yalpstore.BuildConfig;
import com.github.yeriomin.yalpstore.Util;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class BugReportSenderFtp extends BugReportSender {

    static private final String FTP_HOST = "yalp-store-crash-reports.duckdns.org";
    static private final int FTP_PORT = 1021;
    static private final String FTP_USER = "crashes";
    static private final String FTP_PASSWORD = "nopassword";

    public BugReportSenderFtp(Context context) {
        super(context);
    }

    @Override
    protected void compose() {
        super.compose();
        files.add(new BugReportMessageBuilder(context)
            .setIdentification(userIdentification)
            .setStackTrace(stackTrace)
            .setMessage(userMessage)
            .setFromDeviceDefinitionRequest(fromDeviceDefinitionRequest)
            .build()
            .getFile()
        );
    }

    @Override
    public boolean send() {
        compose();
        Log.i(BugReportSenderFtp.class.getSimpleName(), "Uploading");
        boolean result = uploadAll();
        Log.i(BugReportSenderFtp.class.getSimpleName(), "Done");
        return result;
    }

    private boolean uploadAll() {
        FTPClient ftpClient = new FTPClient();
        try {
            ftpClient.connect(FTP_HOST, FTP_PORT);
            if (!ftpClient.login(FTP_USER, FTP_PASSWORD)) {
                return false;
            }
            String dirName = getDirName();
            ftpClient.makeDirectory(dirName);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE, FTP.BINARY_FILE_TYPE);
            ftpClient.setFileTransferMode(FTP.BINARY_FILE_TYPE);
            boolean result = true;
            for (File file: files) {
                result &= upload(ftpClient, file, dirName + "/" + file.getName());
            }
            return result;
        } catch (IOException e) {
            Log.e(BugReportSenderFtp.class.getSimpleName(), "FTP network error: " + e.getMessage());
        } finally {
            closeSilently(ftpClient);
        }
        return false;
    }

    static private boolean upload(FTPClient client, File file, String destination) {
        FileInputStream fileInputStream;
        try {
            fileInputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            return false;
        }
        try {
            return client.storeFile(destination, fileInputStream);
        } catch (IOException e) {
            Log.e(BugReportSenderFtp.class.getSimpleName(), client.getReplyString());
            Log.e(BugReportSenderFtp.class.getSimpleName(), "FTP network error: " + e.getMessage());
        } finally {
            Util.closeSilently(fileInputStream);
        }
        return false;
    }

    static private void closeSilently(FTPClient ftpClient) {
        try {
            ftpClient.logout();
            if (ftpClient.isConnected()) {
                ftpClient.disconnect();
            }
        } catch (IOException e) {
            // Ignore
        }
    }

    private String getDirName() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.getDefault());
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        return format.format(Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTimeInMillis())
            + "-" + BuildConfig.VERSION_NAME
            + "-" + Build.DEVICE.replace("-", "_")
        ;
    }
}
