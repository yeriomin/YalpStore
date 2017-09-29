package com.github.yeriomin.playstoreapi;

import okhttp3.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

class OkHttpClientAdapter extends HttpClientAdapter {

    OkHttpClient client;

    public OkHttpClientAdapter() {
        setClient(new OkHttpClient.Builder()
            .connectTimeout(6, TimeUnit.SECONDS)
            .readTimeout(6, TimeUnit.SECONDS)
            .cookieJar(new CookieJar() {
                private final HashMap<HttpUrl, List<Cookie>> cookieStore = new HashMap<HttpUrl, List<Cookie>>();

                public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                    cookieStore.put(url, cookies);
                }

                public List<Cookie> loadForRequest(HttpUrl url) {
                    List<Cookie> cookies = cookieStore.get(url);
                    return cookies != null ? cookies : new ArrayList<Cookie>();
                }
            })
            .build()
        );
    }

    public void setClient(OkHttpClient client) {
        this.client = client;
    }

    @Override
    public byte[] getEx(String url, Map<String, List<String>> params, Map<String, String> headers) throws IOException {
        return request(new Request.Builder().url(buildUrlEx(url, params)).get(), headers);
    }

    @Override
    public byte[] get(String url, Map<String, String> params, Map<String, String> headers) throws IOException {
        return request(new Request.Builder().url(buildUrl(url, params)).get(), headers);
    }

    @Override
    public byte[] postWithoutBody(String url, Map<String, String> urlParams, Map<String, String> headers) throws IOException {
        return post(buildUrl(url, urlParams), new HashMap<String, String>(), headers);
    }

    @Override
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

    @Override
    public byte[] post(String url, byte[] body, Map<String, String> headers) throws IOException {
        if (!headers.containsKey("Content-Type")) {
            headers.put("Content-Type", "application/x-protobuf");
        }

        Request.Builder requestBuilder = new Request.Builder()
            .url(url)
            .post(RequestBody.create(MediaType.parse("application/x-protobuf"), body));

        return post(url, requestBuilder, headers);
    }

    byte[] post(String url, Request.Builder requestBuilder, Map<String, String> headers) throws IOException {
        requestBuilder.url(url);

        return request(requestBuilder, headers);
    }

    byte[] request(Request.Builder requestBuilder, Map<String, String> headers) throws IOException {
        Request request = requestBuilder
            .headers(Headers.of(headers))
            .build();
        System.out.println("Requesting: " + request.url().toString());

        Response response = client.newCall(request).execute();

        int code = response.code();
        byte[] content = response.body().bytes();

        if (code == 401 || code == 403) {
            AuthException e = new AuthException("Auth error", code);
            Map<String, String> authResponse = GooglePlayAPI.parseResponse(new String(content));
            if (authResponse.containsKey("Error") && authResponse.get("Error").equals("NeedsBrowser")) {
                e.setTwoFactorUrl(authResponse.get("Url"));
            }
            throw e;
        } else if (code >= 500) {
            throw new GooglePlayException("Server error", code);
        } else if (code >= 400) {
            throw new GooglePlayException("Malformed request", code);
        }

        return content;
    }

    public String buildUrl(String url, Map<String, String> params) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        if (null != params && !params.isEmpty()) {
            for (String name: params.keySet()) {
                urlBuilder.addQueryParameter(name, params.get(name));
            }
        }
        return urlBuilder.build().toString();
    }

    public String buildUrlEx(String url, Map<String, List<String>> params) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        if (null != params && !params.isEmpty()) {
            for (String name: params.keySet()) {
                for (String value: params.get(name)) {
                    urlBuilder.addQueryParameter(name, value);
                }
            }
        }
        return urlBuilder.build().toString();
    }
}
