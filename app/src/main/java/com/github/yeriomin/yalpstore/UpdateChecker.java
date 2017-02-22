package com.github.yeriomin.yalpstore;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class UpdateChecker extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        UpdatableAppsTask task = new UpdatableAppsTask() {
            @Override
            protected void onPostExecute(Throwable e) {
                super.onPostExecute(e);
                if (null != e) {
                    return;
                }
                int updatesCount = this.apps.size();
                Log.i(this.getClass().getName(), "Found updates for " + updatesCount + " apps");
                if (updatesCount == 0) {
                    return;
                }
                notify(context, updatesCount);
            }

            private void notify(Context context, int updatesCount) {
                Intent i = new Intent(context, UpdatableAppsActivity.class);
                i.setAction(Intent.ACTION_VIEW);
                new NotificationUtil(context).show(
                    i,
                    context.getString(R.string.notification_updates_available_title),
                    context.getString(R.string.notification_updates_available_message, updatesCount)
                );
            }
        };
        task.setContext(context);
        task.execute();
    }
}
