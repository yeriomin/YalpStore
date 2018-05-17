package com.dragons.aurora.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class NotificationManagerWrapper {

    protected Context context;
    protected NotificationManager manager;

    public NotificationManagerWrapper(Context context) {
        this.context = context;
        manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    static public NotificationBuilder getBuilder(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return new NotificationBuilderO(context);
        } else {
            return new NotificationBuilderJellybean(context);
        }
    }

    public void show(Intent intent, String title, String message) {
        show(title, getBuilder(context).setIntent(intent).setTitle(title).setMessage(message).build());
    }

    public void show(String title, Notification notification) {
        manager.notify(title.hashCode(), notification);
    }

    public void cancel(String title) {
        manager.cancel(title.hashCode());
    }
}
