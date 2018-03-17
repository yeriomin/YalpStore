package com.github.yeriomin.yalpstore.task.playstore;

import com.github.yeriomin.yalpstore.PlayStoreApiAuthenticator;

import java.io.IOException;

public class RefreshTokenTask extends AppProvidedCredentialsTask {

    @Override
    public void setCaller(PlayStoreTask caller) {
        super.setCaller(caller);
        setProgressIndicator(caller.getProgressIndicator());
    }

    @Override
    protected void payload() throws IOException {
        new PlayStoreApiAuthenticator(context).refreshToken();
    }
}
