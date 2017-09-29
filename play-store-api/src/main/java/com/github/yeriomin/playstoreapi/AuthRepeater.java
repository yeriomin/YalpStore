package com.github.yeriomin.playstoreapi;

import java.io.IOException;

public abstract class AuthRepeater {

    static private final int RETRY_COUNT = 10;
    static private final int RETRY_INTERVAL = 5000;

    private HttpClientAdapter httpClient;

    abstract protected String request() throws IOException;

    public AuthRepeater(HttpClientAdapter httpClient) {
        this.httpClient = httpClient;
    }

    public String getToken() throws IOException {
        AuthRepeaterException are = new AuthRepeaterException("Failed to get auth token after " + RETRY_COUNT + " attempts");
        int retries = RETRY_COUNT;
        while (retries > 0) {
            retries--;
            System.out.println("Attempt #" + (RETRY_COUNT - retries));
            try {
                return request();
            } catch (AuthException e) {
                if (retries > 0) {
                    sleep();
                } else {
                    are = new AuthRepeaterException("Failed to get auth token after " + RETRY_COUNT + " attempts", e);
                    are.setCode(e.getCode());
                }
            }
        }
        throw are;
    }

    static private void sleep() {
        try {
            Thread.sleep(RETRY_INTERVAL);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
}
