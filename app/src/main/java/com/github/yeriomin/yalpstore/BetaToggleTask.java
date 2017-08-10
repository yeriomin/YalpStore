package com.github.yeriomin.yalpstore;

import com.github.yeriomin.yalpstore.model.App;

import java.io.IOException;

public class BetaToggleTask extends GoogleApiAsyncTask {

    private App app;

    public BetaToggleTask(App app) {
        this.app = app;
    }

    @Override
    protected Throwable doInBackground(String... params) {
        try {
            new PlayStoreApiAuthenticator(context).getApi().testingProgram(app.getPackageName(), !app.isTestingProgramOptedIn());
        } catch (IOException e) {
            return e;
        }
        return null;
    }
}
