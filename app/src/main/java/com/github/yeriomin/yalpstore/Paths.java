package com.github.yeriomin.yalpstore;

import android.content.Context;
import android.os.Environment;
import android.preference.PreferenceManager;

import java.io.File;

public class Paths {

    static public File getYalpPath(Context context) {
        return new File(
            Environment.getExternalStorageDirectory(),
            PreferenceManager.getDefaultSharedPreferences(context).getString(PreferenceActivity.PREFERENCE_DOWNLOAD_DIRECTORY, "")
        );
    }

    static public File getApkPath(Context context, String packageName, int version) {
        String filename = packageName + "." + String.valueOf(version) + ".apk";
        return new File(getYalpPath(context), filename);
    }

    static public File getDeltaPath(Context context, String packageName, int version) {
        return new File(getApkPath(context, packageName, version).getAbsolutePath() + ".delta");
    }

    static public File getObbPath(String packageName, int version, boolean main) {
        File obbDir = new File(new File(Environment.getExternalStorageDirectory(), "Android/obb"), packageName);
        String filename = (main ? "main" : "patch") + "." + String.valueOf(version) + "." + packageName + ".obb";
        return new File(obbDir, filename);
    }
}
