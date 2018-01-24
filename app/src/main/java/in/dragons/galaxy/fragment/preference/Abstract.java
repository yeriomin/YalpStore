package in.dragons.galaxy.fragment.preference;

import in.dragons.galaxy.PreferenceActivity;

public abstract class Abstract {

    protected PreferenceActivity activity;

    abstract public void draw();

    public Abstract(PreferenceActivity activity) {
        this.activity = activity;
    }
}
