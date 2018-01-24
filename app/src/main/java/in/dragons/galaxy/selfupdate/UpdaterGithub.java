package in.dragons.galaxy.selfupdate;

import android.content.Context;

class UpdaterGithub extends Updater {

    public UpdaterGithub(Context context) {
        super(context);
    }

    @Override
    public String getUrlString(int versionCode) {
        return "https://github.com/whyorean/Galaxy/releases/download/0." + versionCode + "/in.dragons.galaxy_" + versionCode + ".apk";
    }
}
