package com.github.yeriomin.yalpstore.task;

import android.os.AsyncTask;

import eu.chainfire.libsuperuser.Shell;

public class RebootTask extends AsyncTask<Void, Void, Void> {

    @Override
    protected Void doInBackground(Void... params) {
        Shell.SU.run("reboot");
        return null;
    }
}
