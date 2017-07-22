package com.github.yeriomin.yalpstore.bugreport;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.github.yeriomin.yalpstore.BuildConfig;
import com.github.yeriomin.yalpstore.FileProvider;
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
