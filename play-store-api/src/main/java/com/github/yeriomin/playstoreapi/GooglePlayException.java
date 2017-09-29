package com.github.yeriomin.playstoreapi;

import java.io.IOException;

public class GooglePlayException extends IOException {

	protected int code;

	public GooglePlayException(String message) {
		super(message);
	}

	public GooglePlayException(String message, int code) {
		super(message);
		this.code = code;
	}

	public GooglePlayException(String message, Throwable cause) {
		super(message, cause);
	}

	public void setCode(int code) {
		this.code = code;
	}

	public int getCode() {
		return this.code;
	}
}
