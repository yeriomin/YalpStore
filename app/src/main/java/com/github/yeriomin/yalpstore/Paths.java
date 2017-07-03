package com.github.yeriomin.yalpstore;

import android.os.Environment;

import java.io.File;

public class Paths {

    static public File getYalpPath() {
        return new File(Environment.getExternalStorageDirectory(), "Download");
    }

    static public File getApkPath(String packageName, int version) {
        String filename = packageName + "." + String.valueOf(version) + ".apk";
        return new File(getYalpPath(), filename);
    }

    static public File getDeltaPath(String packageName, int version) {
        return new File(getApkPath(packageName, version).getAbsolutePath() + ".delta");
    }

    static public File getObbPath(String packageName, int version, boolean main) {
        File obbDir = new File(new File(Environment.getExternalStorageDirectory(), "Android/obb"), packageName);
        String filename = (main ? "main" : "patch") + "." + String.valueOf(version) + "." + packageName + ".obb";
        return new File(obbDir, filename);
    }
}
