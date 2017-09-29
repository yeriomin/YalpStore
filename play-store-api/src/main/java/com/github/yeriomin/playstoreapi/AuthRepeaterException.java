package com.github.yeriomin.playstoreapi;

public class AuthRepeaterException extends AuthException {

    public AuthRepeaterException(String message) {
        super(message);
    }

    public AuthRepeaterException(String message, int code) {
        super(message, code);
    }

    public AuthRepeaterException(String message, Throwable cause) {
        super(message, cause);
    }
}
