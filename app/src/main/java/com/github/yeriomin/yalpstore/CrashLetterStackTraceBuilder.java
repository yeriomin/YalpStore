package com.github.yeriomin.yalpstore;

import android.content.Context;

import java.io.File;
import java.io.IOException;


public class CrashLetterStackTraceBuilder extends CrashLetterBuilder {

    private String stackTrace;

    public CrashLetterStackTraceBuilder(Context context) {
        super(context);
    }

    public CrashLetterStackTraceBuilder setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
        return this;
    }

    @Override
    protected String build() {
        return stackTrace;
    }

    @Override
    protected File getFile() {
        try {
            return File.createTempFile("stacktrace-", ".txt");
        } catch (IOException e) {
            return null;
        }
    }
}
