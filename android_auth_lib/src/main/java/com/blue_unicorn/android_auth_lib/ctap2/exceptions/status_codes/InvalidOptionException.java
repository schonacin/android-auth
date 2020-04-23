package com.blue_unicorn.android_auth_lib.ctap2.exceptions.status_codes;

import com.blue_unicorn.android_auth_lib.ctap2.exceptions.StatusCodeException;

public class InvalidOptionException extends StatusCodeException {

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
