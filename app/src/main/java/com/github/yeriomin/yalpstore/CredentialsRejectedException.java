package com.github.yeriomin.yalpstore;

public class CredentialsRejectedException extends CredentialsException {

    public CredentialsRejectedException() {
        super();
    }

    public CredentialsRejectedException(String message) {
        super(message);
    }

    public CredentialsRejectedException(String message, Throwable cause) {
        super(message, cause);
    }

    public CredentialsRejectedException(Throwable cause) {
        super(cause);
    }
}
