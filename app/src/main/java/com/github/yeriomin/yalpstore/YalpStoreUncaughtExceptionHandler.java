package com.github.yeriomin.yalpstore;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
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
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.fromParts("mailto", activity.getString(R.string.about_developer_email), null));
        emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, activity.getPackageName() + " Crash Report");
        emailIntent.putExtra(Intent.EXTRA_TEXT, buildBody(e));
        try {
            activity.startActivity(emailIntent);
        } catch (ActivityNotFoundException ee) {
            Log.e(getClass().getName(), ee.getClass().toString() + " " + ee.getMessage());
            ((ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE)).setText(buildBody(e));
        }
    }

    private String buildBody(Throwable e) {
        StringBuilder body = new StringBuilder();
        if (null != e) {
            body.append("\n\n").append(Log.getStackTraceString(e)).append("\n\n");
        }
        Map<String, String> values = new LinkedHashMap<>();
        values.putAll(getBuildValues());
        values.putAll(getConfigurationValues());
        values.putAll(getDisplayMetricsValues());
        values.putAll(getPackageManagerValues());
        for (String key : values.keySet()) {
            body.append(key).append(" = ").append(values.get(key)).append("\n");
        }
        return body.toString();
    }

    private Map<String, String> getBuildValues() {
        Map<String, String> values = new LinkedHashMap<>();
        values.put("Build.FINGERPRINT", Build.FINGERPRINT);
        values.put("Build.HARDWARE", Build.HARDWARE);
        values.put("Build.BRAND", Build.BRAND);
        values.put("Build.RADIO", Build.RADIO);
        values.put("Build.BOOTLOADER", Build.BOOTLOADER);
        values.put("Build.DEVICE", Build.DEVICE);
        values.put("Build.VERSION.SDK_INT", Integer.toString(Build.VERSION.SDK_INT));
        values.put("Build.MODEL", Build.MODEL);
        values.put("Build.MANUFACTURER", Build.MANUFACTURER);
        values.put("Build.PRODUCT", Build.PRODUCT);
        return values;
    }

    private Map<String, String> getConfigurationValues() {
        Map<String, String> values = new LinkedHashMap<>();
        Configuration config = activity.getResources().getConfiguration();
        values.put("TouchScreen", Integer.toString(config.touchscreen));
        values.put("Keyboard", Integer.toString(config.keyboard));
        values.put("Navigation", Integer.toString(config.navigation));
        values.put("ScreenLayout", Integer.toString(config.screenLayout & 15));
        values.put("HasHardKeyboard", Boolean.toString(config.keyboard == Configuration.KEYBOARD_QWERTY));
        values.put("HasFiveWayNavigation", Boolean.toString(config.navigation == Configuration.NAVIGATIONHIDDEN_YES));
        return values;
    }

    private Map<String, String> getDisplayMetricsValues() {
        Map<String, String> values = new LinkedHashMap<>();
        DisplayMetrics metrics = activity.getResources().getDisplayMetrics();
        values.put("Screen.Density", Integer.toString((int) (metrics.density * 160f)));
        values.put("Screen.Width", Integer.toString(metrics.widthPixels));
        values.put("Screen.Height", Integer.toString(metrics.heightPixels));
        return values;
    }

    private Map<String, String> getPackageManagerValues() {
        Map<String, String> values = new LinkedHashMap<>();
        values.put("Platforms", TextUtils.join(",", NativeDeviceInfoProvider.getPlatforms()));
        values.put("SharedLibraries", TextUtils.join(",", NativeDeviceInfoProvider.getSharedLibraries(activity)));
        values.put("Features", TextUtils.join(",", activity.getPackageManager().getSystemSharedLibraryNames()));
        values.put("Locales", TextUtils.join(",", NativeDeviceInfoProvider.getLocales()));
        return values;
    }
}
