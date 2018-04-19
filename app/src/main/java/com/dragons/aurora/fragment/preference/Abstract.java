package com.dragons.aurora.fragment.preference;

import com.dragons.aurora.fragment.PreferenceFragment;

public abstract class Abstract {

    protected PreferenceFragment activity;

    abstract public void draw();

    public Abstract(PreferenceFragment activity) {
        this.activity = activity;
    }
}
