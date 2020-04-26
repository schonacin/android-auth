package com.blue_unicorn.android_auth_lib.ctap2.exceptions.status_codes;

import com.blue_unicorn.android_auth_lib.ctap2.exceptions.StatusCodeException;

public class MissingParameterException extends StatusCodeException {

    public MissingParameterException() {
    }

    public MissingParameterException(String message) {
        super(message);
    }

    public MissingParameterException(String message, Throwable cause) {
        super(message, cause);
    }

    public MissingParameterException(Throwable cause) {
        super(cause);
    }
}
