package com.dragons.aurora;

import android.content.Context;
import android.util.Log;

import com.dragons.aurora.model.App;

import java.io.File;
import java.io.IOException;

public class DeltaPatcherGDiff extends DeltaPatcherAbstract {

    public DeltaPatcherGDiff(Context context, App app) {
        super(context, app);
    }

    @Override
    public boolean patch() {
        Log.i(DeltaPatcherGDiff.class.getSimpleName(), "Preparing to apply delta patch to " + app.getPackageName());
        File originalApk = InstalledApkCopier.getCurrentApk(app);
        if (null == originalApk || !originalApk.exists()) {
            Log.e(DeltaPatcherGDiff.class.getSimpleName(), "Could not find existing apk to patch it: " + originalApk);
            return false;
        }
        Log.i(DeltaPatcherGDiff.class.getSimpleName(), "Patching with " + patch);
        com.nothome.delta.GDiffPatcher patcher = new com.nothome.delta.GDiffPatcher();
        try {
            File destinationApk = Paths.getApkPath(context, app.getPackageName(), app.getVersionCode());
            patcher.patch(originalApk, patch, destinationApk);
            Log.i(DeltaPatcherGDiff.class.getSimpleName(), "Patching successfully completed");
            return true;
        } catch (IOException e) {
            Log.e(DeltaPatcherGDiff.class.getSimpleName(), "Patching failed: " + e.getClass().getName() + " " + e.getMessage());
            return false;
        } finally {
            Log.i(DeltaPatcherGDiff.class.getSimpleName(), "Deleting " + patch);
            patch.delete();
        }
    }
}
