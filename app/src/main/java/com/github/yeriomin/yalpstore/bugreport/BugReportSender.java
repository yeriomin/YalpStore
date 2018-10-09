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
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class BugReportSender {

    protected String stackTrace;
    protected String userMessage;
    protected String userIdentification;
    protected boolean fromDeviceDefinitionRequest;
    protected Context context;
    protected List<File> files = new ArrayList<>();

    abstract public boolean send();

    public BugReportSender setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
        return this;
    }

    public BugReportSender setUserMessage(String userMessage) {
        this.userMessage = userMessage;
        return this;
    }

    public BugReportSender setUserIdentification(String userIdentification) {
        this.userIdentification = userIdentification;
        return this;
    }

    public BugReportSender setFromDeviceDefinitionRequest(boolean fromDeviceDefinitionRequest) {
        this.fromDeviceDefinitionRequest = fromDeviceDefinitionRequest;
        return this;
    }

    public BugReportSender(Context context) {
        this.context = context;
    }

    protected void compose() {
        Log.i(getClass().getSimpleName(), "Composing a report");
        files.add(new BugReportDeviceInfoBuilder(context).build().getFile());
        files.add(new BugReportLogBuilder(context).build().getFile());
        files.add(new BugReportPreferencesBuilder(context).build().getFile());
        if (!TextUtils.isEmpty(stackTrace)) {
            files.add(new BugReportBuilder(context).setFileName("stacktrace.txt").setContent(stackTrace).build().getFile());
        }
    }
}
