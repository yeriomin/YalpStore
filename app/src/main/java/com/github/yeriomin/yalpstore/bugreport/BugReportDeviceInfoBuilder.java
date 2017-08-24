package com.github.yeriomin.yalpstore.bugreport;

import android.app.ActivityManager;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.github.yeriomin.yalpstore.EglExtensionRetriever;
import com.github.yeriomin.yalpstore.NativeDeviceInfoProvider;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;

import static com.github.yeriomin.yalpstore.NativeDeviceInfoProvider.GOOGLE_SERVICES_PACKAGE_ID;
import static com.github.yeriomin.yalpstore.NativeDeviceInfoProvider.GOOGLE_VENDING_PACKAGE_ID;

public class BugReportDeviceInfoBuilder extends BugReportBuilder {

    static private Map<String, String> staticProperties = new HashMap<>();

    static {
        staticProperties.put("Client", "android-google");
        staticProperties.put("Roaming", "mobile-notroaming");
        staticProperties.put("TimeZone", TimeZone.getDefault().getID());
        staticProperties.put("GL.Extensions", TextUtils.join(",", EglExtensionRetriever.getEglExtensions()));
    }

    public BugReportDeviceInfoBuilder(Context context) {
        super(context);
        setFileName("device-" + Build.DEVICE + ".properties");
    }

    @Override
    public BugReportDeviceInfoBuilder build() {
        StringBuilder body = new StringBuilder();
        Map<String, String> deviceInfo = getDeviceInfo();
        for (String key : deviceInfo.keySet()) {
            body.append(key).append(" = ").append(deviceInfo.get(key)).append("\n");
        }
        body.append("\n\n");
        setContent(body.toString());
        super.build();
        return this;
    }

    private Map<String, String> getDeviceInfo() {
        Map<String, String> values = new LinkedHashMap<>();
        values.put("UserReadableName", Build.MANUFACTURER + " " + Build.PRODUCT + " (api" + Integer.toString(Build.VERSION.SDK_INT) + ")");
        values.putAll(getBuildValues());
        values.putAll(getConfigurationValues());
        values.putAll(getDisplayMetricsValues());
        values.putAll(getPackageManagerValues());
        values.putAll(getOperatorValues());
        values.putAll(staticProperties);
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
        Configuration config = context.getResources().getConfiguration();
        values.put("TouchScreen", Integer.toString(config.touchscreen));
        values.put("Keyboard", Integer.toString(config.keyboard));
        values.put("Navigation", Integer.toString(config.navigation));
        values.put("ScreenLayout", Integer.toString(config.screenLayout & 15));
        values.put("HasHardKeyboard", Boolean.toString(config.keyboard == Configuration.KEYBOARD_QWERTY));
        values.put("HasFiveWayNavigation", Boolean.toString(config.navigation == Configuration.NAVIGATIONHIDDEN_YES));
        values.put("GL.Version", Integer.toString(((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getDeviceConfigurationInfo().reqGlEsVersion));
        return values;
    }

    private Map<String, String> getDisplayMetricsValues() {
        Map<String, String> values = new LinkedHashMap<>();
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        values.put("Screen.Density", Integer.toString((int) (metrics.density * 160f)));
        values.put("Screen.Width", Integer.toString(metrics.widthPixels));
        values.put("Screen.Height", Integer.toString(metrics.heightPixels));
        return values;
    }

    private Map<String, String> getPackageManagerValues() {
        Map<String, String> values = new LinkedHashMap<>();
        values.put("Platforms", TextUtils.join(",", NativeDeviceInfoProvider.getPlatforms()));
        values.put("SharedLibraries", TextUtils.join(",", NativeDeviceInfoProvider.getSharedLibraries(context)));
        values.put("Features", TextUtils.join(",", NativeDeviceInfoProvider.getFeatures(context)));
        values.put("Locales", TextUtils.join(",", NativeDeviceInfoProvider.getLocales(context)));
        values.put("GSF.version", Integer.toString(NativeDeviceInfoProvider.getVersionCode(context, GOOGLE_SERVICES_PACKAGE_ID, 0)));
        values.put("Vending.version", Integer.toString(NativeDeviceInfoProvider.getVersionCode(context, GOOGLE_VENDING_PACKAGE_ID, 0)));
        return values;
    }

    private Map<String, String> getOperatorValues() {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        Map<String, String> values = new LinkedHashMap<>();
        values.put("CellOperator", tm.getNetworkOperator());
        values.put("SimOperator", tm.getSimOperator());
        return values;
    }
}
