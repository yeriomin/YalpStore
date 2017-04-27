package com.github.yeriomin.yalpstore;

import android.app.Activity;
import android.content.Intent;

class YalpStoreUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

    static public final String INTENT_MESSAGE = "INTENT_MESSAGE";

    private Activity activity;

    public YalpStoreUncaughtExceptionHandler(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void uncaughtException(Thread t, final Throwable e) {
        e.printStackTrace();
        Intent errorIntent = new Intent(activity.getApplicationContext(), CrashLetterActivity.class);
        errorIntent.putExtra(INTENT_MESSAGE, new CrashLetterBuilder(activity).build(e));
        errorIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.getApplicationContext().startActivity(errorIntent);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(2);
    }
}
