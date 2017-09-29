package com.github.yeriomin.playstoreapi;

public class TokenDispenserException extends GooglePlayException {

    public TokenDispenserException(String message) {
        super(message);
    }

    public TokenDispenserException(Throwable cause) {
        super(null, cause);
    }
}
