package com.dragons.aurora.fragment.preference;

import com.dragons.aurora.fragment.PreferenceFragment;

public abstract class Abstract {

    protected PreferenceFragment activity;

    public Abstract(PreferenceFragment activity) {
        this.activity = activity;
    }

    abstract public void draw();
}
