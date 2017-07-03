package com.github.yeriomin.yalpstore;

import android.util.Log;

import com.github.yeriomin.yalpstore.model.App;

import java.io.File;

abstract public class DeltaPatcherAbstract {

    protected App app;
    protected File patch;

    public DeltaPatcherAbstract(App app) {
        Log.i(getClass().getName(), "Chosen delta patcher");
        this.app = app;
        patch = Paths.getDeltaPath(app.getPackageName(), app.getVersionCode());
    }

    abstract boolean patch();
}
