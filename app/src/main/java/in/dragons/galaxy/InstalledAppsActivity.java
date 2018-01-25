package in.dragons.galaxy;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import in.dragons.galaxy.fragment.FilterMenu;
import in.dragons.galaxy.model.App;
import in.dragons.galaxy.task.AppListValidityCheckTask;
import in.dragons.galaxy.task.ForegroundInstalledAppsTask;
import in.dragons.galaxy.view.InstalledAppBadge;
import in.dragons.galaxy.view.ListItem;

public class InstalledAppsActivity extends AppListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityOptions.makeSceneTransitionAnimation(this).toBundle();
        setTitle(R.string.activity_title_updates_and_other_apps);
        Button button =(Button) findViewById(R.id.main_button);
        TextView textView = (TextView) findViewById(R.id.main_button_txt);
        CardView cardView = (CardView) findViewById(R.id.list_card);
        button.setEnabled(true);
        textView.setEnabled(true);
        button.setVisibility(View.VISIBLE);
        textView.setVisibility(View.VISIBLE);
        cardView.setVisibility(View.VISIBLE);
        button.setText(R.string.list_check_updates);
        textView.setText(R.string.list_check_updates_txt);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), UpdatableAppsActivity.class));

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppListValidityCheckTask task = new AppListValidityCheckTask(this);
        task.setIncludeSystemApps(new FilterMenu(this).getFilterPreferences().isSystemApps());
        task.execute();
    }

    @Override
    public void loadApps() {
        new ForegroundInstalledAppsTask(this).execute();
    }

    @Override
    protected ListItem getListItem(App app) {
        InstalledAppBadge appBadge = new InstalledAppBadge();
        appBadge.setApp(app);
        return appBadge;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        menu.findItem(R.id.action_filter).setVisible(true);
        menu.findItem(R.id.filter_system_apps).setVisible(true);
        return result;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.findItem(R.id.action_flag).setVisible(false);
    }
}
