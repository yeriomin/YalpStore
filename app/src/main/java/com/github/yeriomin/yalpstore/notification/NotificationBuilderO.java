package com.github.yeriomin.yalpstore.notification;

import android.app.NotificationManager;
import android.content.Context;

class NotificationBuilderO extends NotificationBuilderJellybean {

    public NotificationBuilderO(Context context) {
        super(context);
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }
}
