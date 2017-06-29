package com.github.yeriomin.yalpstore;

import android.util.Log;

import com.github.yeriomin.yalpstore.model.App;

import java.io.File;
import java.io.IOException;

public class DeltaPatcherGDiff extends DeltaPatcherAbstract {

    public DeltaPatcherGDiff(App app) {
        super(app);
    }

    @Override
    public boolean patch() {
        Log.i(DeltaPatcherGDiff.class.getName(), "Preparing to apply delta patch to " + app.getPackageName());
        File originalApk = InstalledApkCopier.getCurrentApk(app);
        if (!originalApk.exists()) {
            Log.e(DeltaPatcherGDiff.class.getName(), "Could not find existing apk to patch it");
            return false;
        }
        Log.i(DeltaPatcherGDiff.class.getName(), "Patching with " + patch);
        com.nothome.delta.GDiffPatcher patcher = new com.nothome.delta.GDiffPatcher();
        try {
            File destinationApk = Downloader.getApkPath(app.getPackageName(), app.getVersionCode());
            patcher.patch(originalApk, patch, destinationApk);
            Log.i(DeltaPatcherGDiff.class.getName(), "Patching successfully completed");
            return true;
        } catch (IOException e) {
            Log.e(DeltaPatcherGDiff.class.getName(), "Patching failed: " + e.getClass().getName() + " " + e.getMessage());
            return false;
        } finally {
            Log.i(DeltaPatcherGDiff.class.getName(), "Deleting " + patch);
            patch.delete();
        }
    }
}
