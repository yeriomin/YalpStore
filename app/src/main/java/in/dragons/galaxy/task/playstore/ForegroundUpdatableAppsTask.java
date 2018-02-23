package in.dragons.galaxy.task.playstore;

import android.app.DownloadManager;
import android.database.Cursor;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.yeriomin.playstoreapi.GooglePlayAPI;

import java.io.IOException;
import java.util.List;

import in.dragons.galaxy.BlackWhiteListManager;
import in.dragons.galaxy.BuildConfig;
import in.dragons.galaxy.GalaxyPermissionManager;
import in.dragons.galaxy.R;
import in.dragons.galaxy.UpdatableAppsActivity;
import in.dragons.galaxy.model.App;

import static android.content.Context.DOWNLOAD_SERVICE;

public class ForegroundUpdatableAppsTask extends UpdatableAppsTask implements CloneableTask {

    private UpdatableAppsActivity activity;
    private Button update, cancel;
    private TextView textView;
    private DownloadManager.Query query;
    private DownloadManager dm;

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
            activity.findViewById(R.id.unicorn).setVisibility(View.VISIBLE);
            activity.findViewById(R.id.update_card).setVisibility(View.GONE);
        }
        textView = (TextView) activity.findViewById(R.id.updates_txt);
        textView.setText("Update(s) available : " + updatableApps.size());
        toggleUpdateAll(!updatableApps.isEmpty());
    }

    private void toggleUpdateAll(boolean enable) {
        update = (Button) activity.findViewById(R.id.update_all);
        cancel = (Button) activity.findViewById(R.id.update_cancel);
        textView = (TextView) activity.findViewById(R.id.updates_txt);

        if (enable) {
            activity.findViewById(R.id.update_card).setVisibility(View.VISIBLE);
            update.setVisibility(View.VISIBLE);
            textView.setVisibility(View.VISIBLE);
        }

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GalaxyPermissionManager permissionManager = new GalaxyPermissionManager(activity);
                if (permissionManager.checkPermission()) {
                    activity.launchUpdateAll();
                    update.setVisibility(View.GONE);
                    cancel.setVisibility(View.VISIBLE);
                    textView.setText(R.string.list_updating);
                } else {
                    permissionManager.requestPermission();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                query = new DownloadManager.Query();
                query.setFilterByStatus(DownloadManager.STATUS_PENDING | DownloadManager.STATUS_RUNNING);
                dm = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
                Cursor c = dm.query(query);
                while (c.moveToNext() == true) {
                    dm.remove(c.getLong(c.getColumnIndex(DownloadManager.COLUMN_ID)));
                }
                update.setVisibility(View.VISIBLE);
                cancel.setVisibility(View.GONE);
                textView.setText("Update(s) available : " + updatableApps.size());
            }
        });
    }
}
