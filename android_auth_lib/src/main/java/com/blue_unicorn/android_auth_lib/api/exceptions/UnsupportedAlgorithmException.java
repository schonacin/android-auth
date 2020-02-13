package com.blue_unicorn.android_auth_lib.api.exceptions;

public class UnsupportedAlgorithmException extends AuthenticatorException {

    public UnsupportedAlgorithmException() {
    }

    public UnsupportedAlgorithmException(String message) {
        super(message);
    }

    public UnsupportedAlgorithmException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsupportedAlgorithmException(Throwable cause) {
        super(cause);
    }

}
