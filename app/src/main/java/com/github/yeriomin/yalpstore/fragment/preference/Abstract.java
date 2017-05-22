package com.github.yeriomin.yalpstore.fragment.preference;

import com.github.yeriomin.yalpstore.PreferenceActivity;

public abstract class Abstract {

    protected PreferenceActivity activity;

    abstract public void draw();

    public Abstract(PreferenceActivity activity) {
        this.activity = activity;
    }
}
