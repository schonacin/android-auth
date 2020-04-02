package com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.exceptions;

public class OtherException extends AuthenticatorException {

    public OtherException() {
    }

    public OtherException(String message) {
        super(message);
    }

    public OtherException(String message, Throwable cause) {
        super(message, cause);
    }

    public OtherException(Throwable cause) {
        super(cause);
    }
}
