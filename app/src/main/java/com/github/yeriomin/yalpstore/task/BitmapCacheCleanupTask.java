package com.github.yeriomin.yalpstore.task;

import android.content.Context;
import android.os.AsyncTask;

import com.github.yeriomin.yalpstore.BitmapManager;

import java.io.File;
import java.lang.ref.WeakReference;

public class BitmapCacheCleanupTask extends AsyncTask<Void, Void, Void> {

    private WeakReference<Context> contextRef = new WeakReference<>(null);

    public BitmapCacheCleanupTask(Context context) {
        this.contextRef = new WeakReference<>(context);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        if (null == contextRef.get()) {
            return null;
        }
        for (File file: contextRef.get().getCacheDir().listFiles()) {
            if (isStale(file)) {
                file.delete();
            }
        }
        return null;
    }

    private boolean isStale(File file) {
        return file.getName().endsWith(".png")
            && file.lastModified() + BitmapManager.VALID_MILLIS < System.currentTimeMillis()
        ;
    }
}
