package com.blue_unicorn.android_auth_lib.ctap2.exceptions.status_codes;

import com.blue_unicorn.android_auth_lib.ctap2.constants.StatusCode;
import com.blue_unicorn.android_auth_lib.ctap2.exceptions.StatusCodeException;

public class InvalidParameterException extends StatusCodeException {

    public InvalidParameterException() {
        super(StatusCode.CTAP1_ERR_INVALID_PARAMETER);
    }

    public InvalidParameterException(String message) {
        super(StatusCode.CTAP1_ERR_INVALID_PARAMETER, message);
    }

    public InvalidParameterException(String message, Throwable cause) {
        super(StatusCode.CTAP1_ERR_INVALID_PARAMETER, message, cause);
    }

    public InvalidParameterException(Throwable cause) {
        super(StatusCode.CTAP1_ERR_INVALID_PARAMETER, cause);
    }
}
