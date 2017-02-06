package com.github.yeriomin.yalpstore;

import java.io.IOException;

public class TokenDispenserException extends IOException {

    public TokenDispenserException(String message) {
        super(message);
    }

    public TokenDispenserException(Throwable cause) {
        super(cause);
    }
}
