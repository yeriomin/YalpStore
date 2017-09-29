package com.github.yeriomin.playstoreapi;

import java.util.Map;

public class MockGooglePlayAPI extends GooglePlayAPI {

    @Override
    protected Map<String, String> getDefaultLoginParams(String email, String password) throws GooglePlayException {
        Map<String, String> params = super.getDefaultLoginParams(email, password);
        params.remove("EncryptedPasswd");
        params.put("Passwd", password);
        return params;
    }
}
