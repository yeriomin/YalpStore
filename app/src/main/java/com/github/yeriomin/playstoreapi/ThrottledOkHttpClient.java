package com.github.yeriomin.playstoreapi;

import android.os.SystemClock;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

class ThrottledOkHttpClient {

    private static final long DEFAULT_REQUEST_INTERVAL = 2000;

    private long lastRequestTime;
    private long requestInterval = DEFAULT_REQUEST_INTERVAL;
    private OkHttpClient client;

    public void setRequestInterval(long requestInterval) {
        this.requestInterval = requestInterval;
    }

    public ThrottledOkHttpClient() {
        this.client = new OkHttpClient.Builder()
            .connectTimeout(3, TimeUnit.SECONDS)
            .readTimeout(3, TimeUnit.SECONDS)
            .cookieJar(new CookieJar() {
                private final HashMap<HttpUrl, List<Cookie>> cookieStore = new HashMap<>();

                @Override
                public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                    cookieStore.put(url, cookies);
                }

                @Override
                public List<Cookie> loadForRequest(HttpUrl url) {
                    List<Cookie> cookies = cookieStore.get(url);
                    return cookies != null ? cookies : new ArrayList<Cookie>();
                }
            })
            .build();
    }

    public byte[] get(String url, Map<String, String> params) throws IOException {
        return get(url, params, null);
    }

    public byte[] get(String url, Map<String, String> params, Map<String, String> headers) throws IOException {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        if (null != params && !params.isEmpty()) {
            for (String name: params.keySet()) {
                urlBuilder.addQueryParameter(name, params.get(name));
            }
        }

        Request.Builder requestBuilder = new Request.Builder()
            .url(urlBuilder.build())
            .get();

        return request(requestBuilder, headers);
    }

    public byte[] post(String url, Map<String, String> params, Map<String, String> headers) throws IOException {
        headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");

        FormBody.Builder bodyBuilder = new FormBody.Builder();
        if (null != params && !params.isEmpty()) {
            for (String name: params.keySet()) {
                bodyBuilder.add(name, params.get(name));
            }
        }

        Request.Builder requestBuilder = new Request.Builder()
            .url(url)
            .post(bodyBuilder.build());

        return post(url, requestBuilder, headers);
    }

    public byte[] post(String url, byte[] body, Map<String, String> headers) throws IOException {
        if (!headers.containsKey("Content-Type")) {
            headers.put("Content-Type", "application/x-protobuf");
        }

        Request.Builder requestBuilder = new Request.Builder()
            .url(url)
            .post(RequestBody.create(MediaType.parse("application/x-protobuf"), body));

        return post(url, requestBuilder, headers);
    }

    private byte[] post(String url, Request.Builder requestBuilder, Map<String, String> headers) throws IOException {
        requestBuilder.url(url);

        return request(requestBuilder, headers);
    }

    private byte[] request(Request.Builder requestBuilder, Map<String, String> headers) throws IOException {
        if (this.lastRequestTime > 0) {
            long msecRemaining = this.requestInterval - System.currentTimeMillis() + this.lastRequestTime;
            if (msecRemaining > 0) {
                SystemClock.sleep(msecRemaining);
            }
        }

        Request request = requestBuilder
            .headers(Headers.of(headers))
            .build();
        System.out.println("Requesting: " + request.url().toString());

        Response response = client.newCall(request).execute();

        int code = response.code();
        byte[] content = response.body().bytes();

        if (code >= 400) {
            throw new GooglePlayException(String.valueOf(code) + " Probably an auth error: " + new String(content), code);
        }

        return content;
    }
}
