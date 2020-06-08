package com.blue_unicorn.android_auth_lib.ctap2.exceptions.status_codes;

import com.blue_unicorn.android_auth_lib.ctap2.constants.StatusCode;
import com.blue_unicorn.android_auth_lib.ctap2.exceptions.StatusCodeException;

public class InvalidOptionException extends StatusCodeException {

    public InvalidOptionException() {
        super(StatusCode.CTAP2_ERR_INVALID_OPTION);
    }

    public InvalidOptionException(String message) {
        super(StatusCode.CTAP2_ERR_INVALID_OPTION, message);
    }

    public InvalidOptionException(String message, Throwable cause) {
        super(StatusCode.CTAP2_ERR_INVALID_OPTION, message, cause);
    }

    public InvalidOptionException(Throwable cause) {
        super(StatusCode.CTAP2_ERR_INVALID_OPTION, cause);
    }

}
