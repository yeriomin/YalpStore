package com.github.yeriomin.yalpstore;

import android.text.TextUtils;
import android.util.Log;

import com.github.yeriomin.yalpstore.model.App;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class InstalledApkCopier {

    static public boolean copy(App app) {
        String currentApkPath = null;
        if (null != app.getPackageInfo() && null != app.getPackageInfo().applicationInfo) {
            currentApkPath = app.getPackageInfo().applicationInfo.sourceDir;
        }
        if (TextUtils.isEmpty(currentApkPath)) {
            Log.e(InstalledApkCopier.class.getName(), "applicationInfo.sourceDir is empty");
            return false;
        }
        File destination = Downloader.getApkPath(app.getPackageName(), app.getVersionCode());
        if (destination.exists()) {
            Log.i(InstalledApkCopier.class.getName(), destination.toString() + " exists");
            return true;
        }
        copy(new File(currentApkPath), destination);
        return true;
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

            in.close();
            out.flush();
            out.close();
        } catch (IOException e) {
            Log.e(InstalledApkCopier.class.getName(), e.getClass().getName() + " " + e.getMessage());
        } finally {
            try {
                if (null != in) {
                    in.close();
                }
                if (null != out) {
                    out.close();
                }
            } catch (IOException e) {
                // Could not close
            }
        }
    }
}
