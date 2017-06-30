package com.github.yeriomin.yalpstore.notification;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
class NotificationBuilderIcs extends NotificationBuilderHoneycomb {

    @Override
    public NotificationBuilder setProgress(int max, int progress) {
        builder.setProgress(max, progress, false);
        return this;
    }

    public NotificationBuilderIcs(Context context) {
        super(context);
    }
}
