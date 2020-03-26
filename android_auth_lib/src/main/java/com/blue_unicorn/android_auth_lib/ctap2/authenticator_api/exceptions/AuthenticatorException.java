package com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.exceptions;

public class AuthenticatorException extends Exception {

    public AuthenticatorException() {
    }

    public AuthenticatorException(String message) {
        super(message);
    }

    public AuthenticatorException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthenticatorException(Throwable cause) {
        super(cause);
    }

}
