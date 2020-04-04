package com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.exceptions;

/**
 * AuthLibException is the generic exception class for this library and not associated with any error codes related to WebAuthn and CTAP2
 */
public class AuthLibException extends Exception {

    public AuthLibException() {
    }

    public AuthLibException(String message) {
        super(message);
    }

    public AuthLibException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthLibException(Throwable cause) {
        super(cause);
    }
}
