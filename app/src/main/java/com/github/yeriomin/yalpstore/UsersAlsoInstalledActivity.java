package com.github.yeriomin.yalpstore;

public class UsersAlsoInstalledActivity extends DetailsDependentActivity {

    @Override
    protected void loadApps() {
        addApps(app.getUsersAlsoInstalledApps());
    }
}
