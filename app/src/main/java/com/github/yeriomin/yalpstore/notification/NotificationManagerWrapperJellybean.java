package com.github.yeriomin.yalpstore.notification;

import android.annotation.TargetApi;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class NotificationManagerWrapperJellybean extends NotificationManagerWrapperBuilder {

    public NotificationManagerWrapperJellybean(Context context) {
        super(context);
    }

    @Override
    protected Notification get(Intent intent, String title, String message) {
        return getBuilder(intent, title, message).build();
    }
}
