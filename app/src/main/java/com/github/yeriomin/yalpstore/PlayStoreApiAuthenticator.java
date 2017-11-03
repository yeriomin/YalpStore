package com.github.yeriomin.yalpstore;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.github.yeriomin.playstoreapi.ApiBuilderException;
import com.github.yeriomin.playstoreapi.AuthException;
import com.github.yeriomin.playstoreapi.DeviceInfoProvider;
import com.github.yeriomin.playstoreapi.GooglePlayAPI;
import com.github.yeriomin.playstoreapi.PropertiesDeviceInfoProvider;
import com.github.yeriomin.playstoreapi.TokenDispenserException;
import com.github.yeriomin.yalpstore.model.LoginInfo;

import java.io.IOException;
import java.util.Locale;

public class PlayStoreApiAuthenticator {

    static private final int RETRIES = 5;

    private Context context;

    private static GooglePlayAPI api;

    public PlayStoreApiAuthenticator(Context context) {
        this.context = context;
    }

    public GooglePlayAPI getApi() throws CredentialsEmptyException {
        if (api == null) {
            api = build();
        }
        return api;
    }

    public void login() throws IOException {
        build(new LoginInfo());
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(PreferenceActivity.PREFERENCE_APP_PROVIDED_EMAIL, true).commit();
    }

    public void login(String email, String password) throws IOException {
        LoginInfo loginInfo = new LoginInfo();
        loginInfo.setEmail(email);
        loginInfo.setPassword(password);
        build(loginInfo);
        PreferenceManager.getDefaultSharedPreferences(context).edit().remove(PreferenceActivity.PREFERENCE_APP_PROVIDED_EMAIL).commit();
    }

    public void logout() {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
            .remove(PreferenceActivity.PREFERENCE_EMAIL)
            .remove(PreferenceActivity.PREFERENCE_GSF_ID)
            .remove(PreferenceActivity.PREFERENCE_AUTH_TOKEN)
            .remove(PreferenceActivity.PREFERENCE_APP_PROVIDED_EMAIL)
            .commit()
        ;
        api = null;
    }

    private GooglePlayAPI build() throws CredentialsEmptyException {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String email = prefs.getString(PreferenceActivity.PREFERENCE_EMAIL, "");
        if (TextUtils.isEmpty(email)) {
            throw new CredentialsEmptyException();
        }
        LoginInfo loginInfo = new LoginInfo();
        loginInfo.setEmail(email);
        try {
            return build(loginInfo);
        } catch (CredentialsEmptyException e) {
            throw e;
        } catch (IOException e) {
            throw new RuntimeException("IOException while building api object from preferences: " + e.getMessage(), e);
        }
    }

    private GooglePlayAPI build(LoginInfo loginInfo) throws IOException {
        api = build(loginInfo, TextUtils.isEmpty(loginInfo.getEmail()) ? RETRIES : 1);
        loginInfo.setGsfId(api.getGsfId());
        loginInfo.setToken(api.getToken());
        save(loginInfo);
        return api;
    }

    private GooglePlayAPI build(LoginInfo loginInfo, int retries) throws IOException {
        int tried = 0;
        TokenDispenserMirrors tokenDispenserMirrors = new TokenDispenserMirrors();
        while (tried < retries) {
            try {
                Log.i(getClass().getName(), "Login attempt #" + (tried + 1));
                com.github.yeriomin.playstoreapi.PlayStoreApiBuilder builder = getBuilder(loginInfo, tokenDispenserMirrors.get());
                GooglePlayAPI api = builder.build();
                loginInfo.setEmail(builder.getEmail());
                return api;
            } catch (ApiBuilderException e) {
                Log.i(getClass().getName(), "ApiBuilderException: " + e.getMessage());
            } catch (AuthException | TokenDispenserException e) {
                tried++;
                if (tried >= retries) {
                    throw e;
                }
            }
        }
        return null;
    }

    private com.github.yeriomin.playstoreapi.PlayStoreApiBuilder getBuilder(LoginInfo loginInfo, String tokenDispenserUrl) {
        fill(loginInfo);
        return new com.github.yeriomin.playstoreapi.PlayStoreApiBuilder()
            .setHttpClient(BuildConfig.DEBUG ? new DebugHttpClientAdapter() : new NativeHttpClientAdapter())
            .setDeviceInfoProvider(getDeviceInfoProvider())
            .setLocale(loginInfo.getLocale())
            .setEmail(loginInfo.getEmail())
            .setPassword(loginInfo.getPassword())
            .setGsfId(loginInfo.getGsfId())
            .setToken(loginInfo.getToken())
            .setTokenDispenserUrl(tokenDispenserUrl)
        ;
    }

    private DeviceInfoProvider getDeviceInfoProvider() {
        DeviceInfoProvider deviceInfoProvider;
        String spoofDevice = PreferenceManager.getDefaultSharedPreferences(context)
            .getString(PreferenceActivity.PREFERENCE_DEVICE_TO_PRETEND_TO_BE, "")
        ;
        if (TextUtils.isEmpty(spoofDevice)) {
            deviceInfoProvider = new NativeDeviceInfoProvider();
            ((NativeDeviceInfoProvider) deviceInfoProvider).setContext(context);
            ((NativeDeviceInfoProvider) deviceInfoProvider).setLocaleString(Locale.getDefault().toString());
        } else {
            deviceInfoProvider = new PropertiesDeviceInfoProvider();
            ((PropertiesDeviceInfoProvider) deviceInfoProvider).setProperties(new SpoofDeviceManager(context).getProperties(spoofDevice));
            ((PropertiesDeviceInfoProvider) deviceInfoProvider).setLocaleString(Locale.getDefault().toString());
        }
        return deviceInfoProvider;
    }

    private void fill(LoginInfo loginInfo) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String locale = prefs.getString(PreferenceActivity.PREFERENCE_REQUESTED_LANGUAGE, "");
        loginInfo.setLocale(TextUtils.isEmpty(locale) ? Locale.getDefault() : new Locale(locale));
        loginInfo.setGsfId(prefs.getString(PreferenceActivity.PREFERENCE_GSF_ID, ""));
        loginInfo.setToken(prefs.getString(PreferenceActivity.PREFERENCE_AUTH_TOKEN, ""));
    }

    private void save(LoginInfo loginInfo) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
            .putString(PreferenceActivity.PREFERENCE_EMAIL, loginInfo.getEmail())
            .putString(PreferenceActivity.PREFERENCE_GSF_ID, loginInfo.getGsfId())
            .putString(PreferenceActivity.PREFERENCE_AUTH_TOKEN, loginInfo.getToken())
            .commit()
        ;
    }
}
