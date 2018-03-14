package com.github.yeriomin.yalpstore;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

import com.github.yeriomin.yalpstore.model.App;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class InstalledApkCopier {

    static public boolean copy(Context context, App app) {
        File destination = getDestination(context, app);
        if (destination.exists()) {
            Log.i(InstalledApkCopier.class.getSimpleName(), destination.toString() + " exists");
            return true;
        }
        File currentApk = getCurrentApk(app);
        if (null == currentApk) {
            Log.e(InstalledApkCopier.class.getSimpleName(), "applicationInfo.sourceDir is empty");
            return false;
        }
        if (!currentApk.exists()) {
            Log.e(InstalledApkCopier.class.getSimpleName(), currentApk + " does not exist");
            return false;
        }
        return copy(currentApk, destination);
    }

    static public File getCurrentApk(App app) {
        if (null != app.getPackageInfo() && null != app.getPackageInfo().applicationInfo) {
            return new File(app.getPackageInfo().applicationInfo.sourceDir);
        }
        return null;
    }

    static private boolean copy(File input, File output) {
        InputStream in = null;
        OutputStream out = null;
        File dir = output.getParentFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }
        try {
            in = new FileInputStream(input);
            out = new FileOutputStream(output);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }

            out.flush();
            return true;
        } catch (IOException e) {
            Log.e(InstalledApkCopier.class.getSimpleName(), e.getClass().getName() + " " + e.getMessage());
            return false;
        } finally {
            Util.closeSilently(in);
            Util.closeSilently(out);
        }
    }

    static private File getDestination(Context context, App app) {
        return new File(
            new File(
                Paths.getStorageRoot(context),
                PreferenceManager.getDefaultSharedPreferences(context).getString(PreferenceActivity.PREFERENCE_DOWNLOAD_DIRECTORY, "")
            ),
            app.getPackageName() + "." + String.valueOf(app.getInstalledVersionCode()) + ".apk"
        );
    }
}
