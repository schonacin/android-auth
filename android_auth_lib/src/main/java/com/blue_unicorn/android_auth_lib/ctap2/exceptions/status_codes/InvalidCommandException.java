package com.blue_unicorn.android_auth_lib.ctap2.exceptions.status_codes;

import com.blue_unicorn.android_auth_lib.ctap2.constants.StatusCode;
import com.blue_unicorn.android_auth_lib.ctap2.exceptions.StatusCodeException;

public class InvalidCommandException extends StatusCodeException {

    public InvalidCommandException() {
        super(StatusCode.CTAP1_ERR_INVALID_COMMAND);
    }

    public InvalidCommandException(String message) {
        super(StatusCode.CTAP1_ERR_INVALID_COMMAND, message);
    }

    public InvalidCommandException(String message, Throwable cause) {
        super(StatusCode.CTAP1_ERR_INVALID_COMMAND, message, cause);
    }

    public InvalidCommandException(Throwable cause) {
        super(StatusCode.CTAP1_ERR_INVALID_COMMAND, cause);
    }
}
