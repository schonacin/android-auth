package com.blue_unicorn.android_auth_lib.ctap2.exceptions.status_codes;

import com.blue_unicorn.android_auth_lib.ctap2.exceptions.StatusCodeException;

public class InvalidCommandException extends StatusCodeException {

    public InvalidCommandException() {
    }

    public InvalidCommandException(String message) {
        super(message);
    }

    public InvalidCommandException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidCommandException(Throwable cause) {
        super(cause);
    }
}
