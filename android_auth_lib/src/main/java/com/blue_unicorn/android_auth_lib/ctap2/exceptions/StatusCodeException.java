package com.blue_unicorn.android_auth_lib.ctap2.exceptions;

/**
 * StatusCodeException acts as a superclass to the different error response values in the
 * <a href="https://fidoalliance.org/specs/fido-v2.0-id-20180227/fido-client-to-authenticator-protocol-v2.0-id-20180227.html#error-responses">status codes</a>
 */
public abstract class StatusCodeException extends Exception {

    //TODO: add status codes + create respective constant classes
    // also think about best location (in the specification it's under Message Encoding
    // but the API Methods need to have access to them as well

    // TODO: think of better name

    public StatusCodeException() {
    }

    public StatusCodeException(String message) {
        super(message);
    }

    public StatusCodeException(String message, Throwable cause) {
        super(message, cause);
    }

    public StatusCodeException(Throwable cause) {
        super(cause);
    }

}
