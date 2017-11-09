package com.github.yeriomin.yalpstore.selfupdate;

import android.content.Context;

class UpdaterFdroid extends Updater {

    public UpdaterFdroid(Context context) {
        super(context);
    }

    @Override
    public String getUrlString(int versionCode) {
        return "https://f-droid.org/repo/com.github.yeriomin.yalpstore_" + versionCode + ".apk";
    }
}
