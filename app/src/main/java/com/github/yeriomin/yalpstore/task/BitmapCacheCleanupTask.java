package com.github.yeriomin.yalpstore.task;

import android.content.Context;

import com.github.yeriomin.yalpstore.BitmapManager;

import java.io.File;

public class BitmapCacheCleanupTask extends CleanupTask {

    public BitmapCacheCleanupTask(Context context) {
        super(context);
    }

    @Override
    protected boolean shouldDelete(File file) {
        return file.getName().endsWith(".png")
            && file.lastModified() + BitmapManager.VALID_MILLIS < System.currentTimeMillis()
        ;
    }

    @Override
    protected File[] getFiles() {
        return contextRef.get().getCacheDir().listFiles();
    }
}
