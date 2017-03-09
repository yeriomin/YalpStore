package com.github.yeriomin.yalpstore;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.github.yeriomin.playstoreapi.ApiBuilderException;
import com.github.yeriomin.playstoreapi.GooglePlayAPI;

import java.io.IOException;
import java.util.Locale;

public class PlayStoreApiAuthenticator {

    private Context context;

    private static GooglePlayAPI api;

    public PlayStoreApiAuthenticator(Context context) {
        this.context = context;
    }

    public GooglePlayAPI getApi() throws IOException {
        if (api == null) {
            api = build();
        }
        return api;
    }

    public void login(String email) throws IOException {
        build(email);
    }

    public void login(String email, String password) throws IOException {
        build(email, password);
    }

    public void logout() {
        SharedPreferences.Editor prefs = PreferenceManager.getDefaultSharedPreferences(context).edit();
        prefs.remove(PreferenceActivity.PREFERENCE_EMAIL);
        prefs.remove(PreferenceActivity.PREFERENCE_AUTH_TOKEN);
        prefs.apply();
        api = null;
    }

    private GooglePlayAPI build() throws IOException {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String email = prefs.getString(PreferenceActivity.PREFERENCE_EMAIL, "");
        return build(email);
    }

    private GooglePlayAPI build(String email) throws IOException {
        return build(email, null);
    }

    private GooglePlayAPI build(String email, String password) throws IOException {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String gsfId = prefs.getString(PreferenceActivity.PREFERENCE_GSF_ID, "");
        String token = prefs.getString(PreferenceActivity.PREFERENCE_AUTH_TOKEN, "");
        if (email.isEmpty()) {
            throw new CredentialsEmptyException();
        }

        NativeDeviceInfoProvider deviceInfoProvider = new NativeDeviceInfoProvider();
        deviceInfoProvider.setContext(context);
        deviceInfoProvider.setLocaleString(Locale.getDefault().toString());
        com.github.yeriomin.playstoreapi.PlayStoreApiBuilder builder = new com.github.yeriomin.playstoreapi.PlayStoreApiBuilder()
            .setDeviceInfoProvider(deviceInfoProvider)
            .setEmail(email)
            ;
        if (null != password) {
            builder.setPassword(password);
        }
        if (!gsfId.isEmpty()) {
            builder.setGsfId(gsfId);
        }
        if (!token.isEmpty()) {
            builder.setToken(token);
        }
        try {
            api = builder.build();
        } catch (ApiBuilderException e) {
            // Should not happen
        }

        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putString(PreferenceActivity.PREFERENCE_EMAIL, email);
        prefsEditor.putString(PreferenceActivity.PREFERENCE_GSF_ID, gsfId);
        prefsEditor.putString(PreferenceActivity.PREFERENCE_AUTH_TOKEN, token);
        prefsEditor.apply();
        return api;
    }
}
