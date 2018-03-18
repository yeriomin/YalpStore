package com.github.yeriomin.yalpstore.notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

abstract public class NotificationBuilder {

    protected Context context;

    abstract public NotificationBuilder setTitle(String title);
    abstract public NotificationBuilder setMessage(String message);
    abstract public NotificationBuilder setIntent(Intent intent);
    abstract public Notification build();

    public NotificationBuilder addAction(int iconId, int stringId, PendingIntent pendingIntent) {
        return this;
    }

    public NotificationBuilder setProgress(int max, int progress) {
        return this;
    }

    public NotificationBuilder(Context context) {
        this.context = context.getApplicationContext();
    }

    protected PendingIntent getPendingIntent(Intent intent) {
        return isServiceIntent(intent) ? PendingIntent.getService(context, 1, intent, 0) : PendingIntent.getActivity(context, 1, intent, 0);
    }

    private boolean isServiceIntent(Intent intent) {
        return null != intent.getComponent() && intent.getComponent().getClassName().contains("Service");
    }
}
