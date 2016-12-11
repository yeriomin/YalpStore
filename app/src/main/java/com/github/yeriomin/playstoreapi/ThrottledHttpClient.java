package com.github.yeriomin.playstoreapi;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class ThrottledHttpClient extends DefaultHttpClient {

    private static final long DEFAULT_REQUEST_INTERVAL = 2000;

    private long lastRequestTime;
    private long requestInterval = DEFAULT_REQUEST_INTERVAL;

    public ThrottledHttpClient(HttpParams httpParams) {
        super(httpParams);
    }

    public void setRequestInterval(long requestInterval) {
        this.requestInterval = requestInterval;
    }

    public byte[] get(String url, Map<String, String> params) throws IOException {
        return get(url, params, null);
    }

    public byte[] get(String url, Map<String, String> params, Map<String, String> headers) throws IOException {
        if (!params.isEmpty()) {
            url = url + "?" + URLEncodedUtils.format(getNameValuePairList(params), "UTF-8");
        }
        HttpRequestBase request = new HttpGet(url);
        return request(request, headers);
    }

    public byte[] post(String url, Map<String, String> params, Map<String, String> headers) throws IOException {
        try {
            headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            return post(url, new UrlEncodedFormEntity(getNameValuePairList(params), "UTF-8"), headers);
        } catch (UnsupportedEncodingException e) {
            // Highly unlikely
            return null;
        }
    }

    public byte[] post(String url, HttpEntity body, Map<String, String> headers) throws IOException {
        HttpPost request = new HttpPost(url);
        request.setEntity(body);
        if (!headers.containsKey("Content-Type")) {
            headers.put("Content-Type", "application/x-protobuf");
        }
        return request(request, headers);
    }

    private byte[] request(HttpRequestBase request, Map<String, String> headers) throws IOException {
        System.out.println("Requesting: " + request.getURI().toString());
        if (this.lastRequestTime > 0) {
            long msecRemaining = this.requestInterval - System.currentTimeMillis() + this.lastRequestTime;
            if (msecRemaining > 0) {
                try {
                    Thread.currentThread().sleep(msecRemaining);
                } catch (InterruptedException e) {
                    // Unlikely
                    System.err.println(e.getMessage());
                }
            }
        }

        if (null != headers) {
            for (String header: headers.keySet()) {
                request.setHeader(header, headers.get(header));
            }
        }

        HttpResponse response = this.execute(request);
        this.lastRequestTime = System.currentTimeMillis();
        int statusCode = response.getStatusLine().getStatusCode();
        boolean isProtobuf = response.containsHeader("Content-Type")
            && response.getHeaders("Content-Type")[0].getValue().contains("protobuf");
        byte[] content = readAll(response.getEntity().getContent());

        if (!isProtobuf) {
            throw new GooglePlayException(String.valueOf(statusCode) + " Thats not even protobuf: " + new String(content), statusCode);
        }

        if (statusCode >= 400) {
            throw new GooglePlayException(String.valueOf(statusCode) + " Probably an auth error: " + new String(content), statusCode);
        }

        return content;
    }

    private static List<NameValuePair> getNameValuePairList(Map<String, String> map) {
        List<NameValuePair> list = new ArrayList<>();
        for (String param: map.keySet()) {
            list.add(new BasicNameValuePair(param, map.get(param)));
        }
        return list;
    }

    private static byte[] readAll(InputStream inputStream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, len);
        }
        return outputStream.toByteArray();
    }
}
