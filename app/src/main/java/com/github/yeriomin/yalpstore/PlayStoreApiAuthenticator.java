/*
 * Yalp Store
 * Copyright (C) 2018 Sergey Yeriomin <yeriomin@gmail.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.github.yeriomin.yalpstore;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.github.yeriomin.playstoreapi.ApiBuilderException;
import com.github.yeriomin.playstoreapi.AuthException;
import com.github.yeriomin.playstoreapi.DeviceInfoProvider;
import com.github.yeriomin.playstoreapi.GooglePlayAPI;
import com.github.yeriomin.playstoreapi.PropertiesDeviceInfoProvider;
import com.github.yeriomin.playstoreapi.TokenDispenserException;
import com.github.yeriomin.yalpstore.model.LoginInfo;
import com.github.yeriomin.yalpstore.model.LoginInfoDao;
import com.github.yeriomin.yalpstore.task.playstore.BackgroundCategoryTask;
import com.github.yeriomin.yalpstore.task.playstore.PlayStorePayloadTask;
import com.github.yeriomin.yalpstore.task.playstore.PlayStoreTask;
import com.github.yeriomin.yalpstore.task.playstore.UserProfileTask;
import com.github.yeriomin.yalpstore.task.playstore.WishlistUpdateTask;

import java.io.IOException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class PlayStoreApiAuthenticator {

    public static final String PREFERENCE_USER_ID = "PREFERENCE_USER_ID";

    static private final int RETRIES = 5;

    private Context context;
    private Set<PlayStorePayloadTask> onLoginTasks = new HashSet<>();

    private static GooglePlayAPI api;
    private static TokenDispenserMirrors tokenDispenserMirrors = new TokenDispenserMirrors();

    public static void forceRelogin() {
        api = null;
    }

    public PlayStoreApiAuthenticator(Context context) {
        this.context = context;
        BackgroundCategoryTask categoryTask = new BackgroundCategoryTask();
        categoryTask.setManager(new CategoryManager(context));
        onLoginTasks.add(categoryTask);
        onLoginTasks.add(new WishlistUpdateTask());
        onLoginTasks.add(new UserProfileTask());
    }

    public GooglePlayAPI getApi() throws IOException {
        if (api == null) {
            if (!YalpStoreApplication.user.isLoggedIn()) {
                throw new CredentialsEmptyException();
            }
            api = build(YalpStoreApplication.user);
        }
        return api;
    }

    public void login(LoginInfo loginInfo) throws IOException {
        api = build(loginInfo);
    }

    public void login() throws IOException {
        YalpStoreApplication.user.setTokenDispenserUrl(tokenDispenserMirrors.get());
        login(YalpStoreApplication.user);
    }

    public void refreshToken() throws IOException {
        if (!YalpStoreApplication.user.isLoggedIn()) {
            throw new CredentialsEmptyException();
        }
        YalpStoreApplication.user.setToken(null);
        login(YalpStoreApplication.user);
    }

    public void logout() {
        logout(false);
    }

    public void logout(boolean andDelete) {
        if (andDelete) {
            SQLiteDatabase db = new SqliteHelper(context).getWritableDatabase();
            new LoginInfoDao(db).delete(YalpStoreApplication.user);
            db.close();
        }
        YalpStoreApplication.user.clear();
        PreferenceUtil.getDefaultSharedPreferences(context).edit().remove(PREFERENCE_USER_ID).commit();
        forceRelogin();
    }

    private GooglePlayAPI build(LoginInfo loginInfo) throws IOException {
        api = build(loginInfo, RETRIES);
        loginInfo.setGsfId(api.getGsfId());
        loginInfo.setToken(api.getToken());
        loginInfo.setDfeCookie(api.getDfeCookie());
        loginInfo.setDeviceConfigToken(api.getDeviceConfigToken());
        loginInfo.setDeviceCheckinConsistencyToken(api.getDeviceCheckinConsistencyToken());
        save(loginInfo);
        runOnLoginTasks();
        return api;
    }

    private GooglePlayAPI build(LoginInfo loginInfo, int retries) throws IOException {
        int tried = 0;
        tokenDispenserMirrors.reset();
        while (tried < retries) {
            try {
                com.github.yeriomin.playstoreapi.PlayStoreApiBuilder builder = getBuilder(loginInfo);
                GooglePlayAPI api = builder.build();
                loginInfo.setEmail(builder.getEmail());
                return api;
            } catch (ApiBuilderException e) {
                // Impossible, unless there are mistakes, so no need to make it a declared exception
                throw new RuntimeException(e);
            } catch (AuthException | TokenDispenserException e) {
                if (PlayStoreTask.noNetwork(e.getCause()) && !NetworkUtil.isNetworkAvailable(context)) {
                    throw (IOException) e.getCause();
                }
                if (loginInfo.appProvidedEmail()) {
                    loginInfo.setTokenDispenserUrl(tokenDispenserMirrors.get());
                    loginInfo.setEmail(null);
                    loginInfo.setGsfId(null);
                }
                tried++;
                if (tried >= retries) {
                    throw e;
                }
                Log.i(getClass().getSimpleName(), "Login retry #" + tried);
            }
        }
        throw loginInfo.appProvidedEmail()
            ? new TokenDispenserException("Try again later")
            : new IOException("Unknown error happened during login")
        ;
    }

    private com.github.yeriomin.playstoreapi.PlayStoreApiBuilder getBuilder(LoginInfo loginInfo) {
        return new com.github.yeriomin.playstoreapi.PlayStoreApiBuilder()
            .setHttpClient(BuildConfig.DEBUG ? new DebugHttpClientAdapter() : new NativeHttpClientAdapter())
            .setDeviceInfoProvider(getDeviceInfoProvider(loginInfo.getDeviceDefinitionName()))
            .setLocale(loginInfo.getLocale())
            .setEmail(loginInfo.getEmail())
            .setPassword(loginInfo.getPassword())
            .setGsfId(loginInfo.getGsfId())
            .setToken(loginInfo.getToken())
            .setTokenDispenserUrl(loginInfo.getTokenDispenserUrl())
            .setDeviceCheckinConsistencyToken(loginInfo.getDeviceCheckinConsistencyToken())
            .setDeviceConfigToken(loginInfo.getDeviceConfigToken())
            .setDfeCookie(loginInfo.getDfeCookie())
        ;
    }

    private DeviceInfoProvider getDeviceInfoProvider(String spoofDevice) {
        DeviceInfoProvider deviceInfoProvider;
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

    private void save(LoginInfo loginInfo) {
        if (loginInfo.appProvidedEmail()) {
            loginInfo.setUserName(context.getString(R.string.auth_built_in));
        }
        SQLiteDatabase db = new SqliteHelper(context).getWritableDatabase();
        new LoginInfoDao(db).insert(loginInfo);
        db.close();
        PreferenceUtil.getDefaultSharedPreferences(context).edit().putInt(PREFERENCE_USER_ID, loginInfo.hashCode()).commit();
    }

    private void runOnLoginTasks() {
        for (PlayStorePayloadTask task: onLoginTasks) {
            task.setContext(context);
            task.executeOnExecutorIfPossible();
        }
    }
}
