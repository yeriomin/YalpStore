package com.dragons.aurora.notification;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
class NotificationBuilderIcs extends NotificationBuilderHoneycomb {

    public NotificationBuilderIcs(Context context) {
        super(context);
    }

    @Override
    public NotificationBuilder setProgress(int max, int progress) {
        builder.setProgress(max, progress, false);
        return this;
    }
}
