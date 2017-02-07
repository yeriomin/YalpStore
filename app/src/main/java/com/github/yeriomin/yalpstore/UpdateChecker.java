package com.github.yeriomin.yalpstore;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
                if (null == e) {
                    int updatesCount = this.apps.size();
                    Log.i(this.getClass().getName(), "Found updates for " + updatesCount + " apps");
                    if (updatesCount > 0) {
                        Intent i = new Intent(context, UpdatableAppsActivity.class);
                        i.setAction(Intent.ACTION_VIEW);
                        createNotification(
                            context,
                            i,
                            context.getString(R.string.notification_updates_available_title),
                            context.getString(R.string.notification_updates_available_message, updatesCount)
                        );
                    }
                }
            }
        };
        task.setContext(context);
        task.execute();
    }

    private void createNotification(Context c, Intent i, String title, String message) {
        PendingIntent pendingIntent = PendingIntent.getActivity(c, 1, i, 0);
        Notification notification = NotificationUtil.createNotification(
            c,
            pendingIntent,
            title,
            message,
            R.drawable.ic_notification
        );
        NotificationManager manager = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(title.hashCode(), notification);
    }
}
