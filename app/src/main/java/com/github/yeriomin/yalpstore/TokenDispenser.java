package com.github.yeriomin.yalpstore;

import android.util.Log;

import com.github.yeriomin.playstoreapi.AuthException;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TokenDispenser {

    static private final String DISPENSER_URL = "http://tokendispenser-yeriomin.rhcloud.com/";

    static private final String RESOURCE_TOKEN = "token";
    static private final String RESOURCE_TOKEN_AC2DM = "token-ac2dm";

    static private final String PARAMETER_EMAIL = "email";

    static public String getToken(String email) throws IOException {
        return request(getUrl(email, RESOURCE_TOKEN));
    }

    static public String getTokenAc2dm(String email) throws IOException {
        return request(getUrl(email, RESOURCE_TOKEN_AC2DM));
    }

    static private HttpUrl getUrl(String email, String path) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(DISPENSER_URL).newBuilder();
        urlBuilder.addPathSegment(path);
        urlBuilder.addPathSegment(PARAMETER_EMAIL);
        urlBuilder.addPathSegment(email);
        return urlBuilder.build();
    }

    static private String request(HttpUrl url) throws IOException {
        Log.i(TokenDispenser.class.getName(), "Requesting " + url.toString());
        Request.Builder requestBuilder = new Request.Builder();
        Request request = requestBuilder.url(url).build();
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        OkHttpClient client = builder
            .connectTimeout(1, TimeUnit.MINUTES)
            .readTimeout(1, TimeUnit.MINUTES)
            .writeTimeout(1, TimeUnit.MINUTES)
            .build();
        Response response;
        try {
            response = client.newCall(request).execute();
        } catch (Throwable e) {
            throw new TokenDispenserException(e);
        }
        int code = response.code();
        if (code == 401 || code == 403) {
            throw new AuthException("TokenDispenser failed to get a token");
        } else if (code >= 400) {
            throw new TokenDispenserException("Error " + code);
        }
        return new String(response.body().bytes());
    }
}
