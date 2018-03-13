package com.github.yeriomin.yalpstore.task;

import android.content.Context;

import com.github.yeriomin.yalpstore.BitmapManager;

import java.io.File;

public class OldApkCleanupTask extends CleanupTask {

    static public final long VALID_MILLIS = 1000*60*60*24;

    public OldApkCleanupTask(Context context) {
        super(context);
    }

    @Override
    protected boolean shouldDelete(File file) {
        return file.getName().endsWith(".apk")
            && file.lastModified() + VALID_MILLIS < System.currentTimeMillis()
        ;
    }

    @Override
    protected File[] getFiles() {
        return contextRef.get().getFilesDir().listFiles();
    }
}
