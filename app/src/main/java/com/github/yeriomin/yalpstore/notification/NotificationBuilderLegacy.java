package com.github.yeriomin.yalpstore.notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.github.yeriomin.yalpstore.R;

import java.lang.reflect.Method;

class NotificationBuilderLegacy extends NotificationBuilder {

    private boolean ongoing;
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
            if (ongoing) {
                notification.flags |= Notification.FLAG_ONGOING_EVENT;
                notification.flags |= Notification.FLAG_ONLY_ALERT_ONCE;
            }
        } catch (Exception e) {
            // do nothing
        }
        return notification;
    }

    @Override
    public NotificationBuilder setProgress(int max, int progress) {
        ongoing = true;
        return super.setProgress(max, progress);
    }

    public NotificationBuilderLegacy(Context context) {
        super(context);
    }
}
