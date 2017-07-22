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

    public BugReportSender(Context context) {
        this.context = context;
    }

    protected void compose() {
        Log.i(getClass().getName(), "Composing a report");
        files.add(new BugReportDeviceInfoBuilder(context).build().getFile());
        files.add(new BugReportLogBuilder(context).build().getFile());
        files.add(new BugReportPreferencesBuilder(context).build().getFile());
        if (!TextUtils.isEmpty(stackTrace)) {
            files.add(new BugReportBuilder(context).setFileName("stacktrace.txt").setContent(stackTrace).build().getFile());
        }
    }
}
