package com.github.yeriomin.yalpstore;

import com.github.yeriomin.playstoreapi.AuthException;

public class CredentialsEmptyException extends AuthException {

    public CredentialsEmptyException() {
        super("CredentialsEmptyException");
    }

    public CredentialsEmptyException(String message) {
        super(message);
    }

    public CredentialsEmptyException(String message, Throwable cause) {
        super(message, cause);
    }
}
