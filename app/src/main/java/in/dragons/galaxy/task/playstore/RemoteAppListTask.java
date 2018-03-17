package in.dragons.galaxy.task.playstore;

import com.github.yeriomin.playstoreapi.BulkDetailsEntry;
import com.github.yeriomin.playstoreapi.GooglePlayAPI;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import in.dragons.galaxy.model.App;
import in.dragons.galaxy.model.AppBuilder;

public class RemoteAppListTask extends PlayStorePayloadTask<List<App>> {

    @Override
    protected List<App> getResult(GooglePlayAPI api, String... packageNames) throws IOException {
        return getRemoteAppList(api, Arrays.asList(packageNames));
    }

    private List<App> getRemoteAppList(GooglePlayAPI api, List<String> packageNames) throws IOException {
        List<App> apps = new ArrayList<>();
        for (BulkDetailsEntry details : api.bulkDetails(packageNames).getEntryList()) {
            if (!details.hasDoc()) {
                continue;
            }
            apps.add(AppBuilder.build(details.getDoc()));
        }
        Collections.sort(apps);
        return apps;
    }
}
