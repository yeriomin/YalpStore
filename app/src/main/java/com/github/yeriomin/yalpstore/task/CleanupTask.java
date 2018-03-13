package com.github.yeriomin.yalpstore.task;

import android.content.Context;
import android.os.AsyncTask;

import com.github.yeriomin.yalpstore.BitmapManager;

import java.io.File;
import java.lang.ref.WeakReference;

abstract public class CleanupTask extends AsyncTask<Void, Void, Void> {

    protected WeakReference<Context> contextRef = new WeakReference<>(null);

    public CleanupTask(Context context) {
        this.contextRef = new WeakReference<>(context);
    }

    abstract protected boolean shouldDelete(File file);
    abstract protected File[] getFiles();

    @Override
    protected Void doInBackground(Void... voids) {
        if (null == contextRef.get()) {
            return null;
        }
        for (File file: getFiles()) {
            if (shouldDelete(file)) {
                file.delete();
            }
        }
        return null;
    }
}
