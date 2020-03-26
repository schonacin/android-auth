package com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.exceptions;

public class InvalidOptionException extends AuthenticatorException {

    public InvalidOptionException() {
    }

    public InvalidOptionException(String message) {
        super(message);
    }

    public InvalidOptionException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidOptionException(Throwable cause) {
        super(cause);
    }

}
