package com.github.yeriomin.yalpstore;

import android.content.Context;
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
        File destination = Paths.getApkPath(context, app.getPackageName(), app.getInstalledVersionCode());
        if (destination.exists()) {
            Log.i(InstalledApkCopier.class.getName(), destination.toString() + " exists");
            return true;
        }
        File currentApk = getCurrentApk(app);
        if (null == currentApk) {
            Log.e(InstalledApkCopier.class.getName(), "applicationInfo.sourceDir is empty");
            return false;
        }
        if (!currentApk.exists()) {
            Log.e(InstalledApkCopier.class.getName(), currentApk + " does not exist");
            return false;
        }
        copy(currentApk, destination);
        return true;
    }

    static public File getCurrentApk(App app) {
        if (null != app.getPackageInfo() && null != app.getPackageInfo().applicationInfo) {
            return new File(app.getPackageInfo().applicationInfo.sourceDir);
        }
        return null;
    }

    static private void copy(File input, File output) {
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
        } catch (IOException e) {
            Log.e(InstalledApkCopier.class.getName(), e.getClass().getName() + " " + e.getMessage());
        } finally {
            Util.closeSilently(in);
            Util.closeSilently(out);
        }
    }
}
