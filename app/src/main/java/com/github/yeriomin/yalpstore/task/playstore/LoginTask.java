package com.github.yeriomin.yalpstore.task.playstore;

import com.github.yeriomin.yalpstore.PlayStoreApiAuthenticator;

import java.io.IOException;

public class LoginTask extends AppProvidedCredentialsTask {

    @Override
    protected void payload() throws IOException {
        new PlayStoreApiAuthenticator(context).login();
    }
}
