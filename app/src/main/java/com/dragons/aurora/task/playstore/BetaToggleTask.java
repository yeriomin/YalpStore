package com.dragons.aurora.task.playstore;

import com.dragons.aurora.playstoreapiv2.GooglePlayAPI;

import java.io.IOException;

import com.dragons.aurora.model.App;

public class BetaToggleTask extends PlayStorePayloadTask<Void> {

    private App app;

    public BetaToggleTask(App app) {
        this.app = app;
    }

    @Override
    protected Void getResult(GooglePlayAPI api, String... arguments) throws IOException {
        api.testingProgram(app.getPackageName(), !app.isTestingProgramOptedIn());
        return null;
    }
}
