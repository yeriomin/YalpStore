package in.dragons.galaxy.fragment.preference;

import in.dragons.galaxy.fragment.PreferenceFragment;

public abstract class Abstract {

    protected PreferenceFragment activity;

    abstract public void draw();

    public Abstract(PreferenceFragment activity) {
        this.activity = activity;
    }
}
