package com.github.yeriomin.yalpstore.selfupdate;

class UpdaterGithub extends Updater {

    @Override
    public String getUrlString(int versionCode) {
        return "https://github.com/yeriomin/YalpStore/releases/download/0." + versionCode + "/com.github.yeriomin.yalpstore_" + versionCode + ".apk";
    }
}
