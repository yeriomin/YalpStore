package com.github.yeriomin.yalpstore;

public class SimilarAppsActivity extends DetailsDependentActivity {

    @Override
    protected void loadApps() {
        addApps(app.getSimilarApps());
    }
}
