package com.blue_unicorn.android_auth_lib.ctap2.message_encoding.exceptions;

public class AndroidAuthLibException extends Exception {

    public AndroidAuthLibException() {
    }

    public AndroidAuthLibException(String message) {
        super(message);
    }

    public AndroidAuthLibException(String message, Throwable cause) {
        super(message, cause);
    }

    public AndroidAuthLibException(Throwable cause) {
        super(cause);
    }

}
