package com.github.yeriomin.yalpstore.selfupdate;

class UpdaterFdroid extends Updater {

    @Override
    public String getUrlString(int versionCode) {
        return "https://f-droid.org/repo/com.github.yeriomin.yalpstore_" + versionCode + ".apk";
    }
}
