package com.github.yeriomin.yalpstore.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class NotificationManagerWrapper {

    protected Context context;
    protected NotificationManager manager;

    static public NotificationBuilder getBuilder(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            return new NotificationBuilderJellybean(context);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return new NotificationBuilderHoneycomb(context);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            return new NotificationBuilderIcs(context);
        } else {
            return new NotificationBuilderLegacy(context);
        }
    }

    public NotificationManagerWrapper(Context context) {
        this.context = context;
        manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
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
