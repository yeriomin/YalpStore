package com.github.yeriomin.yalpstore;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class SpoofDeviceManager {

    static private final String DEVICES_LIST_KEY = "DEVICE_LIST_" + BuildConfig.VERSION_NAME;
    static private final String SPOOF_FILE_PREFIX = "device-";
    static private final String SPOOF_FILE_SUFFIX = ".properties";

    private Context context;

    public SpoofDeviceManager(Context context) {
        this.context = context;
    }

    public Map<String, String> getDevices() {
        Map<String, String> devices = getDevicesFromSharedPreferences();
        if (devices.isEmpty()) {
            devices = getDevicesFromApk();
            putDevicesToSharedPreferences(devices);
        }
        return devices;
    }

    public Properties getProperties(String entryName) {
        JarFile jarFile = getApkAsJar();
        return getProperties(jarFile, (JarEntry) jarFile.getEntry(entryName));
    }

    private Properties getProperties(JarFile jarFile, JarEntry entry) {
        Properties properties = new Properties();
        try {
            properties.load(jarFile.getInputStream(entry));
        } catch (IOException e) {
            Log.i(getClass().getName(), "Could not read " + entry.getName());
        }
        return properties;
    }

    private Map<String, String> getDevicesFromSharedPreferences() {
        Set<String> deviceNames = Util.getStringSet(context, DEVICES_LIST_KEY);
        Map<String, String> devices = new HashMap<>();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        for (String name: deviceNames) {
            devices.put(name, prefs.getString(name, ""));
        }
        return devices;
    }

    private void putDevicesToSharedPreferences(Map<String, String> devices) {
        Util.putStringSet(context, DEVICES_LIST_KEY, devices.keySet());
        SharedPreferences.Editor prefs = PreferenceManager.getDefaultSharedPreferences(context).edit();
        for (String name: devices.keySet()) {
            prefs.putString(name, devices.get(name));
        }
        prefs.commit();
    }

    private Map<String, String> getDevicesFromApk() {
        JarFile jarFile = getApkAsJar();
        Enumeration<JarEntry> entries = jarFile.entries();
        Map<String, String> devices = new HashMap<>();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            if (!entry.getName().startsWith(SPOOF_FILE_PREFIX) || !entry.getName().endsWith(SPOOF_FILE_SUFFIX)) {
                continue;
            }
            Properties properties = getProperties(jarFile, entry);
            devices.put(entry.getName(), properties.getProperty("UserReadableName"));
        }
        return devices;
    }

    private JarFile getApkAsJar() {
        try {
            return new JarFile(getApkPath());
        } catch (IOException e) {
            // Having a currently running app uninstalled is unlikely
            return null;
        }
    }

    private String getApkPath() {
        try {
            return context.getPackageManager().getApplicationInfo(BuildConfig.APPLICATION_ID, 0).sourceDir;
        } catch (PackageManager.NameNotFoundException e) {
            // Having a currently running app uninstalled is unlikely
            return null;
        }
    }
}
