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
import android.text.TextUtils;

import com.github.yeriomin.yalpstore.BuildConfig;
import com.github.yeriomin.yalpstore.selfupdate.Signature;

import java.util.HashMap;
import java.util.Map;

public class BugReportMessageBuilder extends BugReportPropertiesBuilder {

    private String identification;
    private String message;
    private String stackTrace;
    private boolean fromDeviceDefinitionRequest;

    public BugReportMessageBuilder setIdentification(String identification) {
        this.identification = identification;
        return this;
    }

    public BugReportMessageBuilder setMessage(String message) {
        this.message = message;
        return this;
    }

    public BugReportMessageBuilder setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
        return this;
    }

    public BugReportMessageBuilder setFromDeviceDefinitionRequest(boolean fromDeviceDefinitionRequest) {
        this.fromDeviceDefinitionRequest = fromDeviceDefinitionRequest;
        return this;
    }

    public BugReportMessageBuilder(Context context) {
        super(context);
        setFileName("message.txt");
    }

    @Override
    public BugReportBuilder build() {
        Map<String, String> properties = new HashMap<>();
        properties.put("userId", TextUtils.isEmpty(identification) ? "" : identification);
        properties.put("message", TextUtils.isEmpty(message) ? "" : message);
        properties.put("versionCode", Integer.toString(BuildConfig.VERSION_CODE));
        properties.put("versionName", BuildConfig.VERSION_NAME);
        properties.put("deviceName", Build.DEVICE);
        properties.put("source", getSource());
        properties.put("topic", getTopic());
        setContent(buildProperties(properties));
        super.build();
        return this;
    }

    private String getSource() {
        if (Signature.isFdroid(context)) {
            return "fdroid";
        } else if (Signature.isGithub(context)) {
            return "github";
        }
        return "selfsigned";
    }

    private String getTopic() {
        if (!TextUtils.isEmpty(stackTrace)) {
            return "crash";
        } else if (fromDeviceDefinitionRequest) {
            return "device";
        } else {
            return "feedback";
        }
    }
}
