package in.dragons.galaxy.task.playstore;

import com.dragons.aurora.playstoreapiv2.GooglePlayAPI;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.dragons.galaxy.model.App;

public class DependencyTranslationTask extends RemoteAppListTask {

    protected Map<String, String> translated = new HashMap<>();

    @Override
    protected List<App> getResult(GooglePlayAPI api, String... packageNames) throws IOException {
        List<App> result = super.getResult(api, packageNames);
        for (App app : result) {
            translated.put(app.getPackageName(), app.getDisplayName());
        }
        return result;
    }
}
