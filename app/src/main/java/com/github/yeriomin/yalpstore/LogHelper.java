
package com.github.yeriomin.yalpstore;

import android.util.Log;


public class LogHelper {
    public static final String TAG = "STUDY";
    private static final boolean mLogEnabled = true;

    public static boolean isLogEnabled() {
        return mLogEnabled;
    }

    public static void i(String subTag, String msg) {
        if (mLogEnabled) {
            Log.i(TAG, getLogMsg(subTag, msg));
        }
    }

    public static void i(String subTag, String msg, Throwable tr) {
        if (mLogEnabled) {
            Log.i(TAG, getLogMsg(subTag, msg), tr);
        }
    }

    public static void w(String subTag, String msg) {
        if (mLogEnabled) {
            Log.w(TAG, getLogMsg(subTag, msg));
        }

    }

    public static void w(String subTag, String msg, Throwable tr) {
        if (mLogEnabled) {
            Log.w(TAG, getLogMsg(subTag, msg), tr);
        }

    }

    public static void d(String subTag, String msg) {
        if (mLogEnabled) {
            Log.d(TAG, getLogMsg(subTag, msg));
        }

    }

    public static void d(String subTag, String msg, Throwable tr) {
        if (mLogEnabled) {
            Log.d(TAG, getLogMsg(subTag, msg), tr);
        }

    }

    public static void e(String subTag, String msg) {
        Log.e(TAG, getLogMsg(subTag, msg));
    }

    public static void e(String subTag, String msg, Throwable tr) {
        Log.e(TAG, getLogMsg(subTag, msg), tr);
    }

    private static String getLogMsg(String subTag, String msg) {
        StringBuffer sb = new StringBuffer()
                .append("{").append(Thread.currentThread().getName()).append("}")
                .append("[").append(subTag).append("] ")
                .append(msg);

        return sb.toString();
    }
}
