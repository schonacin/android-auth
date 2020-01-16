package com.blue_unicorn.android_auth_lib.exception;

public class InvalidLengthException extends AndroidAuthLibException {

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
