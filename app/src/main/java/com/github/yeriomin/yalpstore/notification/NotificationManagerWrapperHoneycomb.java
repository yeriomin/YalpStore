package com.github.yeriomin.yalpstore.notification;

import android.annotation.TargetApi;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class NotificationManagerWrapperHoneycomb extends NotificationManagerWrapperBuilder {

    public NotificationManagerWrapperHoneycomb(Context context) {
        super(context);
    }

    @Override
    protected Notification get(Intent intent, String title, String message) {
        return getBuilder(intent, title, message).getNotification();
    }
}
