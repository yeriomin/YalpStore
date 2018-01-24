package in.dragons.galaxy.selfupdate;

import android.content.Context;

class UpdaterFdroid extends Updater {

    public UpdaterFdroid(Context context) {
        super(context);
    }

    @Override
    public String getUrlString(int versionCode) {
        return "https://f-droid.org/repo/in.dragons.galaxy_" + versionCode + ".apk";
    }
}
