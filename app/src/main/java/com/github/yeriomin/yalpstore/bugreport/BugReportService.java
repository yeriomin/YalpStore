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

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class BugReportService extends IntentService {

    static public final String INTENT_IDENTIFICATION = "INTENT_IDENTIFICATION";
    static public final String INTENT_MESSAGE = "INTENT_MESSAGE";
    static public final String INTENT_STACKTRACE = "INTENT_STACKTRACE";
    static public final String INTENT_DEVICE_DEFINITION = "INTENT_DEVICE_DEFINITION";

    static public final String ACTION_SEND_FTP = "ACTION_SEND_FTP";
    static public final String ACTION_SEND_EMAIL = "ACTION_SEND_EMAIL";

    public BugReportService() {
        super("BugReportService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        BugReportSender sender;
        switch (intent.getAction()) {
            case ACTION_SEND_FTP:
                sender = new BugReportSenderFtp(getApplicationContext());
                break;
            case ACTION_SEND_EMAIL:
                sender = new BugReportSenderEmail(getApplicationContext());
                break;
            default:
                Log.e(getClass().getSimpleName(), "Unsupported action: " + intent.getAction());
                return;
        }
        sender
            .setStackTrace(intent.getStringExtra(INTENT_STACKTRACE))
            .setUserMessage(intent.getStringExtra(INTENT_MESSAGE))
            .setUserIdentification(intent.getStringExtra(INTENT_IDENTIFICATION))
            .setFromDeviceDefinitionRequest(intent.getBooleanExtra(INTENT_DEVICE_DEFINITION, false))
            .send()
        ;
    }
}
