package com.github.yeriomin.yalpstore;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.github.yeriomin.yalpstore.bugreport.BugReportService;

class YalpStoreUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

    private Context context;

    public YalpStoreUncaughtExceptionHandler(Context context) {
        this.context = context;
    }

    @Override
    public void uncaughtException(Thread t, final Throwable e) {
        e.printStackTrace();
        Intent errorIntent = new Intent(context.getApplicationContext(), BugReportActivity.class);
        errorIntent.putExtra(BugReportService.INTENT_STACKTRACE, Log.getStackTraceString(e));
        errorIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.getApplicationContext().startActivity(errorIntent);
        System.exit(0);
    }
}
