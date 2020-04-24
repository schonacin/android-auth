package com.blue_unicorn.android_auth_lib.ctap2.exceptions.status_codes;

import com.blue_unicorn.android_auth_lib.ctap2.exceptions.StatusCodeException;

public class InvalidLengthException extends StatusCodeException {

    public InvalidLengthException() {
    }

    public InvalidLengthException(String message) {
        super(message);
    }

    public InvalidLengthException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidLengthException(Throwable cause) {
        super(cause);
    }
}
