package com.github.yeriomin.yalpstore;

public abstract class PreferenceFragment {

    protected PreferenceActivity activity;

    abstract public void draw();

    public PreferenceFragment(PreferenceActivity activity) {
        this.activity = activity;
    }
}
