package com.github.yeriomin.playstoreapi;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class TokenDispenserClient {

    static private final String RESOURCE_EMAIL = "email";
    static private final String RESOURCE_TOKEN = "token";
    static private final String RESOURCE_TOKEN_AC2DM = "token-ac2dm";

    static private final String PARAMETER_EMAIL = "email";

    private String url;
    private HttpClientAdapter httpClient;

    public TokenDispenserClient(String url, HttpClientAdapter httpClient) {
        this.url = url;
        this.httpClient = httpClient;
    }

    public String getRandomEmail() throws IOException {
        return request(httpClient, url + "/" + RESOURCE_EMAIL);
    }

    public String getToken(String email) throws IOException {
        return request(httpClient, getUrl(url, RESOURCE_TOKEN, email));
    }

    public String getTokenAc2dm(String email) throws IOException {
        return request(httpClient, getUrl(url, RESOURCE_TOKEN_AC2DM, email));
    }

    static private String getUrl(String url, String resource, String email) {
        try {
            email = URLEncoder.encode(email, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // Unlikely
        }
        return url + "/" + resource + "/" + PARAMETER_EMAIL + "/" + email;
    }

    static private String request(HttpClientAdapter httpClient, String url) throws IOException {
        try {
            return new String(httpClient.get(url));
        } catch (GooglePlayException e) {
            if (e.getCode() == 404) {
                throw new TokenDispenserException("Token dispenser has no password for " + url);
            } else {
                throw e;
            }
        }
    }
}
