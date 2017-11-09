package com.github.yeriomin.yalpstore.selfupdate;

import android.content.Context;

class UpdaterGithub extends Updater {

    public UpdaterGithub(Context context) {
        super(context);
    }

    @Override
    public String getUrlString(int versionCode) {
        return "https://github.com/yeriomin/YalpStore/releases/download/0." + versionCode + "/com.github.yeriomin.yalpstore_" + versionCode + ".apk";
    }
}
