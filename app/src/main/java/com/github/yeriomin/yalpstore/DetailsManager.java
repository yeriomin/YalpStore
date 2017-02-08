package com.github.yeriomin.yalpstore;

import com.github.yeriomin.yalpstore.model.App;

public abstract class DetailsManager {

    protected DetailsActivity activity;
    protected App app;

    abstract public void draw();

    public DetailsManager(DetailsActivity activity, App app) {
        this.activity = activity;
        this.app = app;
    }
}
