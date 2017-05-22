package com.github.yeriomin.yalpstore.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public abstract class NotificationManagerWrapper {

    protected Context context;
    protected NotificationManager manager;

    abstract protected Notification get(Intent intent, String title, String message);

    public NotificationManagerWrapper(Context context) {
        this.context = context;
        manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void show(Intent intent, String title, String message) {
        manager.notify(title.hashCode(), get(intent, title, message));
    }

    public void cancel(String title) {
        manager.cancel(title.hashCode());
    }

    protected PendingIntent getPendingIntent(Intent intent) {
        return PendingIntent.getActivity(context, 1, intent, 0);
    }
}
