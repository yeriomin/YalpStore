package in.dragons.galaxy.task.playstore;

import com.dragons.aurora.playstoreapiv2.GooglePlayAPI;

import java.io.IOException;

import in.dragons.galaxy.model.App;

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
