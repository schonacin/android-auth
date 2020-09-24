package com.blue_unicorn.android_auth_lib.ctap2.exceptions;

import com.blue_unicorn.android_auth_lib.ctap2.constants.StatusCode;

/**
 * StatusCodeException acts as a superclass to the different error response values in the
 * <a href="https://fidoalliance.org/specs/fido-v2.0-id-20180227/fido-client-to-authenticator-protocol-v2.0-id-20180227.html#error-responses">status codes</a>
 */
public abstract class StatusCodeException extends Exception {

    //TODO: think about best location (in the specification it's under Message Encoding
    // but the API Methods need to have access to them as well)

    private int errorCode;

    public StatusCodeException(@StatusCode int errorCode) {
        this.errorCode = errorCode;
    }

    public StatusCodeException(@StatusCode int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public StatusCodeException(@StatusCode int errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public StatusCodeException(@StatusCode int errorCode, Throwable cause) {
        super(cause);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

}
