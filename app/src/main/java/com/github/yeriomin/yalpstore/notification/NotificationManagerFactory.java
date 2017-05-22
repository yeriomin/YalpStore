package com.github.yeriomin.yalpstore.notification;

import android.content.Context;
import android.os.Build;

public class NotificationManagerFactory {

    static public NotificationManagerWrapper get(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            return new NotificationManagerWrapperJellybean(context);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return new NotificationManagerWrapperHoneycomb(context);
        } else {
            return new NotificationManagerWrapperLegacy(context);
        }
    }
}
