package com.github.yeriomin.yalpstore.task.playstore;

import android.app.Activity;

import com.github.yeriomin.yalpstore.view.AccountTypeDialogBuilder;
import com.github.yeriomin.yalpstore.view.CredentialsDialogBuilder;

import java.io.IOException;

public abstract class AppProvidedCredentialsTask extends CheckCredentialsTask {

    abstract protected void payload() throws IOException;

    @Override
    protected CredentialsDialogBuilder getDialogBuilder() {
        return new AccountTypeDialogBuilder((Activity) context);
    }

    @Override
    protected Void doInBackground(String[] params) {
        try {
            payload();
        } catch (IOException e) {
            exception = e;
        }
        return null;
    }
}
