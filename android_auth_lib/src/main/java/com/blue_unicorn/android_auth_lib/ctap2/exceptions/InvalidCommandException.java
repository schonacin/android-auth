package com.blue_unicorn.android_auth_lib.ctap2.exceptions;

public class InvalidCommandException extends AndroidAuthLibException {

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
