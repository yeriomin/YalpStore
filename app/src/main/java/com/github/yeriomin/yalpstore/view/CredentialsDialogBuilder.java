package com.github.yeriomin.yalpstore.view;

import android.app.Activity;

import com.github.yeriomin.yalpstore.task.playstore.PlayStoreTask;
import com.github.yeriomin.yalpstore.view.DialogWrapper;

abstract public class CredentialsDialogBuilder extends DialogWrapper {

    protected PlayStoreTask caller;

    public CredentialsDialogBuilder(Activity activity) {
        super(activity);
    }

    public void setCaller(PlayStoreTask caller) {
        this.caller = caller;
    }
}
