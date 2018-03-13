package com.github.yeriomin.yalpstore.notification;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Build;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
class NotificationBuilderJellybean extends NotificationBuilderIcs {

    @Override
    public NotificationBuilder addAction(int iconId, int stringId, PendingIntent pendingIntent) {
        builder.addAction(iconId, context.getString(stringId), pendingIntent);
        return this;
    }

    public NotificationBuilderJellybean(Context context) {
        super(context);
    }

    @Override
    public NotificationBuilder setTitle(String title) {
        builder.setPriority(title.hashCode());
        return super.setTitle(title);
    }
}
