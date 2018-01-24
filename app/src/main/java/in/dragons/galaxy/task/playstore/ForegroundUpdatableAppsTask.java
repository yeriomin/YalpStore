package in.dragons.galaxy.task.playstore;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.yeriomin.playstoreapi.GooglePlayAPI;
import in.dragons.galaxy.BlackWhiteListManager;
import in.dragons.galaxy.BuildConfig;
import in.dragons.galaxy.R;
import in.dragons.galaxy.UpdatableAppsActivity;
import in.dragons.galaxy.YalpStoreApplication;
import in.dragons.galaxy.YalpStorePermissionManager;
import in.dragons.galaxy.model.App;
import in.dragons.galaxy.selfupdate.UpdaterFactory;
import in.dragons.galaxy.task.InstalledAppsTask;

import java.io.IOException;
import java.util.List;

public class ForegroundUpdatableAppsTask extends UpdatableAppsTask implements CloneableTask {

    private UpdatableAppsActivity activity;

    public ForegroundUpdatableAppsTask(UpdatableAppsActivity activity) {
        this.activity = activity;
        setContext(activity);
    }

    @Override
    public CloneableTask clone() {
        ForegroundUpdatableAppsTask task = new ForegroundUpdatableAppsTask(this.activity);
        task.setErrorView(errorView);
        task.setProgressIndicator(progressIndicator);
        return task;
    }

    @Override
    protected List<App> getResult(GooglePlayAPI api, String... packageNames) throws IOException {
        super.getResult(api, packageNames);
        if (!new BlackWhiteListManager(context).isUpdatable(BuildConfig.APPLICATION_ID)) {
            return updatableApps;
        }
        int latestVersionCode = UpdaterFactory.get(context).getLatestVersionCode();
        if (latestVersionCode > BuildConfig.VERSION_CODE) {
            App yalp = InstalledAppsTask.getInstalledApp(context.getPackageManager(), BuildConfig.APPLICATION_ID);
            if (null == yalp) {
                return updatableApps;
            }
            yalp.setVersionCode(latestVersionCode);
            yalp.setVersionName("0." + latestVersionCode);
            updatableApps.add(yalp);
        }
        return updatableApps;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.errorView.setText("");
    }

    @Override
    protected void onPostExecute(List<App> result) {
        super.onPostExecute(result);
        activity.clearApps();
        activity.addApps(updatableApps);
        if (success() && updatableApps.isEmpty()) {
            this.errorView.setText(R.string.list_empty_updates);
        }
        toggleUpdateAll(!updatableApps.isEmpty());
    }

    private void toggleUpdateAll(boolean enable) {
        Button button = activity.findViewById(R.id.main_button);
        TextView textView = activity.findViewById(R.id.main_button_txt);
        button.setText(R.string.list_update_all);
        textView.setText(R.string.list_update_all_txt);
        button.setVisibility(enable ? View.VISIBLE : View.GONE);
        textView.setVisibility(enable ? View.VISIBLE : View.GONE);
        activity.findViewById(R.id.list_card).setVisibility(View.VISIBLE);
        if (((YalpStoreApplication) activity.getApplication()).isBackgroundUpdating()) {
            button.setEnabled(false);
            button.setText(R.string.list_updating);
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YalpStorePermissionManager permissionManager = new YalpStorePermissionManager(activity);
                if (permissionManager.checkPermission()) {
                    activity.launchUpdateAll();
                } else {
                    permissionManager.requestPermission();
                }
            }
        });
    }
}
