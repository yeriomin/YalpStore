package com.github.yeriomin.yalpstore.bugreport;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class BugReportService extends IntentService {

    static public final String INTENT_IDENTIFICATION = "INTENT_IDENTIFICATION";
    static public final String INTENT_MESSAGE = "INTENT_MESSAGE";
    static public final String INTENT_STACKTRACE = "INTENT_STACKTRACE";

    static public final String ACTION_SEND_FTP = "ACTION_SEND_FTP";

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
            default:
                Log.e(getClass().getSimpleName(), "Unsupported action: " + intent.getAction());
                return;
        }
        sender
            .setStackTrace(intent.getStringExtra(INTENT_STACKTRACE))
            .setUserMessage(intent.getStringExtra(INTENT_MESSAGE))
            .setUserIdentification(intent.getStringExtra(INTENT_IDENTIFICATION))
            .send()
        ;
    }
}
