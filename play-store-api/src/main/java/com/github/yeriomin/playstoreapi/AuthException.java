package com.github.yeriomin.playstoreapi;

public class AuthException extends GooglePlayException {

    private String twoFactorUrl;

    public AuthException(String message) {
        super(message);
    }

    public AuthException(String message, int code) {
        super(message);
        setCode(code);
    }

    public AuthException(String message, Throwable cause) {
        super(message, cause);
    }

    public String getTwoFactorUrl() {
        return twoFactorUrl;
    }

    public void setTwoFactorUrl(String twoFactorUrl) {
        this.twoFactorUrl = twoFactorUrl;
    }
}
