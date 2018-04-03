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
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;

import com.github.yeriomin.yalpstore.BuildConfig;
import com.github.yeriomin.yalpstore.R;

import java.io.File;
import java.util.ArrayList;

public class BugReportSenderEmail extends BugReportSender {

    public BugReportSenderEmail(Context context) {
        super(context);
    }

    private Uri getUri(File file) {
        return FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileprovider", file);
    }

    @Override
    public boolean send() {
        compose();
        Intent emailIntent = getEmailIntent();
        if (emailIntent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(emailIntent);
            return true;
        }
        return false;
    }

    private Intent getEmailIntent() {
        Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        String developerEmail = context.getString(R.string.about_developer_email);
        emailIntent.setData(Uri.fromParts("mailto", developerEmail, null));
        emailIntent.setType("text/plain");
        emailIntent.setType("message/rfc822");
        emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {developerEmail});
        if (!TextUtils.isEmpty(userMessage)) {
            emailIntent.putExtra(Intent.EXTRA_TEXT, userMessage);
        }
        emailIntent.putExtra(
            Intent.EXTRA_SUBJECT,
            context.getString(
                TextUtils.isEmpty(stackTrace) ? R.string.email_subject_feedback : R.string.email_subject_crash_report,
                BuildConfig.APPLICATION_ID,
                BuildConfig.VERSION_NAME
            )
        );
        ArrayList<Uri> uris = new ArrayList<>();
        for (File file: files) {
            uris.add(getUri(file));
        }
        emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        return emailIntent;
    }
}
