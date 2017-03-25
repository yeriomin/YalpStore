package com.github.yeriomin.yalpstore;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Looper;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import java.util.LinkedHashMap;
import java.util.Map;

class YalpStoreUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

    private Activity activity;

    public YalpStoreUncaughtExceptionHandler(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void uncaughtException(Thread t, final Throwable e) {
        e.printStackTrace();
        Thread thread = new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                showCrashDialog(e);
                Looper.loop();
            }
        };
        try {
            thread.start();
        } catch (Throwable ee) {
            Log.e(getClass().getName(), "Failed to process an uncaught exception: " + ee.getMessage());
            System.exit(1);
        }
    }

    public AlertDialog showCrashDialog(final Throwable e) {
        return new AlertDialog.Builder(activity)
            .setTitle(activity.getString(R.string.dialog_title_application_crashed))
            .setMessage(activity.getString(R.string.dialog_message_application_crashed))
            .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    System.exit(1);
                }
            })
            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    send(e);
                    dialog.dismiss();
                    System.exit(1);
                }
            })
            .show()
        ;
    }

    public void send(Throwable e) {
        String letter = new CrashLetterBuilder(activity).build(e);
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.fromParts("mailto", activity.getString(R.string.about_developer_email), null));
        emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, activity.getPackageName() + " Crash Report");
        emailIntent.putExtra(Intent.EXTRA_TEXT, letter);
        if (emailIntent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivity(emailIntent);
        } else {
            ((ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE)).setText(letter);
        }
    }
}
