package com.github.yeriomin.yalpstore.bugreport;

import android.content.Context;

import com.github.yeriomin.yalpstore.R;

public class BugReportMessageBuilder extends BugReportBuilder {

    private String identification;
    private String message;

    public BugReportMessageBuilder setIdentification(String identification) {
        this.identification = identification;
        return this;
    }

    public BugReportMessageBuilder setMessage(String message) {
        this.message = message;
        return this;
    }

    public BugReportMessageBuilder(Context context) {
        super(context);
        setFileName("message.txt");
    }

    @Override
    public BugReportBuilder build() {
        setContent(context.getString(R.string.bug_report_message, identification, message));
        super.build();
        return this;
    }
}
