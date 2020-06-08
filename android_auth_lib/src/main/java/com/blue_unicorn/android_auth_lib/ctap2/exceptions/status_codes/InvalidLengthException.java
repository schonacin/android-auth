package com.blue_unicorn.android_auth_lib.ctap2.exceptions.status_codes;

import com.blue_unicorn.android_auth_lib.ctap2.constants.StatusCode;
import com.blue_unicorn.android_auth_lib.ctap2.exceptions.StatusCodeException;

public class InvalidLengthException extends StatusCodeException {

    public InvalidLengthException() {
        super(StatusCode.CTAP1_ERR_INVALID_LENGTH);
    }

    public InvalidLengthException(String message) {
        super(StatusCode.CTAP1_ERR_INVALID_LENGTH, message);
    }

    public InvalidLengthException(String message, Throwable cause) {
        super(StatusCode.CTAP1_ERR_INVALID_LENGTH, message, cause);
    }

    public InvalidLengthException(Throwable cause) {
        super(StatusCode.CTAP1_ERR_INVALID_LENGTH, cause);
    }
}
