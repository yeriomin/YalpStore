package com.dragons.aurora.notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.lang.reflect.Method;

import com.dragons.aurora.R;

class NotificationBuilderLegacy extends NotificationBuilder {

    private String title;
    private String message;
    private Intent intent;

    @Override
    public NotificationBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    @Override
    public NotificationBuilder setMessage(String message) {
        this.message = message;
        return this;
    }

    @Override
    public NotificationBuilder setIntent(Intent intent) {
        this.intent = intent;
        return this;
    }

    @Override
    public Notification build() {
        Notification notification = new Notification(R.drawable.ic_notification, "", System.currentTimeMillis());
        try {
            // try to call "setLatestEventInfo" if available
            Method m = notification.getClass().getMethod("setLatestEventInfo", Context.class, CharSequence.class, CharSequence.class, PendingIntent.class);
            m.invoke(notification, context, title, message, getPendingIntent(intent));
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
        } catch (Exception e) {
            // do nothing
        }
        return notification;
    }

    public NotificationBuilderLegacy(Context context) {
        super(context);
    }
}
