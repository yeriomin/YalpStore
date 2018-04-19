package com.dragons.aurora;

import android.content.Context;
import android.util.Log;

import java.io.File;

import com.dragons.aurora.model.App;

abstract public class DeltaPatcherAbstract {

    protected App app;
    protected Context context;
    protected File patch;

    public DeltaPatcherAbstract(Context context, App app) {
        Log.i(getClass().getSimpleName(), "Chosen delta patcher");
        this.app = app;
        this.context = context;
        patch = Paths.getDeltaPath(context, app.getPackageName(), app.getVersionCode());
    }

    abstract boolean patch();
}
