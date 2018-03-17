package in.dragons.galaxy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;

import in.dragons.galaxy.activities.GalaxyActivity;

public class UpdateAllReceiver extends BroadcastReceiver {

    static public final String ACTION_ALL_UPDATES_COMPLETE = "ACTION_ALL_UPDATES_COMPLETE";
    static public final String ACTION_APP_UPDATE_COMPLETE = "ACTION_APP_UPDATE_COMPLETE";

    static public final String EXTRA_PACKAGE_NAME = "EXTRA_PACKAGE_NAME";
    static public final String EXTRA_UPDATE_ACTUALLY_INSTALLED = "EXTRA_UPDATE_ACTUALLY_INSTALLED";

    private GalaxyActivity activity;

    public UpdateAllReceiver(GalaxyActivity activity) {
        this.activity = activity;
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_ALL_UPDATES_COMPLETE);
        filter.addAction(ACTION_APP_UPDATE_COMPLETE);
        activity.registerReceiver(this, filter);
        if (!((GalaxyApplication) activity.getApplication()).isBackgroundUpdating()) {
            enableButton();
        }
        activity.unregisterReceiver(this);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!ContextUtil.isAlive(activity) || TextUtils.isEmpty(intent.getAction())) {
            return;
        }
        if (intent.getAction().equals(ACTION_ALL_UPDATES_COMPLETE)) {
            ((GalaxyApplication) activity.getApplication()).setBackgroundUpdating(false);
            enableButton();
        } else if (intent.getAction().equals(ACTION_APP_UPDATE_COMPLETE)) {
            processAppUpdate(
                    intent.getStringExtra(EXTRA_PACKAGE_NAME),
                    intent.getBooleanExtra(EXTRA_UPDATE_ACTUALLY_INSTALLED, false)
            );
        }
    }

    private void enableButton() {
        Button button = (Button) activity.findViewById(R.id.update_all);
        TextView textView = (TextView) activity.findViewById(R.id.updates_txt);
        if (null != button) {
            button.setEnabled(true);
            textView.setEnabled(true);
            button.setText(R.string.list_update_all);
            textView.setText(R.string.list_update_chk_txt);
        }
    }

    private void processAppUpdate(String packageName, boolean installedUpdate) {
        if (installedUpdate) {
            activity.removeApp(packageName);
        }
    }
}
