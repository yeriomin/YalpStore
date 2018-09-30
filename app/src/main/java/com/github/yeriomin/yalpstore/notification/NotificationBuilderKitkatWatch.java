package com.github.yeriomin.yalpstore.notification;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;

@TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
class NotificationBuilderKitkatWatch extends NotificationBuilderJellybean {

    public NotificationBuilderKitkatWatch(Context context) {
        super(context);
    }

    @Override
    public NotificationBuilder setTitle(String title) {
        builder.setSortKey(title);
        return super.setTitle(title);
    }
}
