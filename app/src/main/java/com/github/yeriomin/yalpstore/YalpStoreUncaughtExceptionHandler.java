package com.github.yeriomin.yalpstore;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

class YalpStoreUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

    static public final String INTENT_MESSAGE = "INTENT_MESSAGE";

    private Context context;

    public YalpStoreUncaughtExceptionHandler(Context context) {
        this.context = context;
    }

    @Override
    public void uncaughtException(Thread t, final Throwable e) {
        e.printStackTrace();
        Intent errorIntent = new Intent(context.getApplicationContext(), CrashLetterActivity.class);
        errorIntent.putExtra(INTENT_MESSAGE, Log.getStackTraceString(e));
        errorIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.getApplicationContext().startActivity(errorIntent);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(2);
    }
}
