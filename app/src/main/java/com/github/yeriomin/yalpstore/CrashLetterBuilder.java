package com.github.yeriomin.yalpstore;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import java.util.LinkedHashMap;
import java.util.Map;

public class CrashLetterBuilder {

    private Activity activity;

    public CrashLetterBuilder(Activity activity) {
        this.activity = activity;
    }

    public String build(Throwable e) {
        StringBuilder body = new StringBuilder();
        if (null != e) {
            body.append("\n\n").append(Log.getStackTraceString(e)).append("\n\n");
        }
        Map<String, String> deviceInfo = getDeviceInfo();
        for (String key : deviceInfo.keySet()) {
            body.append(key).append(" = ").append(deviceInfo.get(key)).append("\n");
        }
        return body.toString();
    }

    private Map<String, String> getDeviceInfo() {
        Map<String, String> values = new LinkedHashMap<>();
        values.put("YALP.VERSION", BuildConfig.VERSION_NAME);
        if (null != DetailsDependentActivity.app) {
            values.put("APP.BEING.INSTALLED", DetailsDependentActivity.app.getPackageName());
        }
        values.putAll(getBuildValues());
        values.putAll(getConfigurationValues());
        values.putAll(getDisplayMetricsValues());
        values.putAll(getPackageManagerValues());
        return values;
    }

    private Map<String, String> getBuildValues() {
        Map<String, String> values = new LinkedHashMap<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            values.put("Build.HARDWARE", Build.HARDWARE);
            values.put("Build.RADIO", Build.RADIO);
            values.put("Build.BOOTLOADER", Build.BOOTLOADER);
        }
        values.put("Build.FINGERPRINT", Build.FINGERPRINT);
        values.put("Build.BRAND", Build.BRAND);
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
