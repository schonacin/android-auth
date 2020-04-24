package com.blue_unicorn.android_auth_lib.ctap2.exceptions.status_codes;

import com.blue_unicorn.android_auth_lib.ctap2.exceptions.StatusCodeException;

public class OtherException extends StatusCodeException {

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
