package com.github.yeriomin.yalpstore.selfupdate;

import android.content.Context;

public class UpdaterFactory {

    static public Updater get(Context context) {
        return Signature.isFdroid(context) ? new UpdaterFdroid(context) : new UpdaterGithub(context);
    }
}
