package com.blue_unicorn.android_auth_lib.ctap2.exceptions.status_codes;

import com.blue_unicorn.android_auth_lib.ctap2.constants.StatusCode;
import com.blue_unicorn.android_auth_lib.ctap2.exceptions.StatusCodeException;

public class MissingParameterException extends StatusCodeException {

    public MissingParameterException() {
        super(StatusCode.CTAP2_ERR_MISSING_PARAMETER);
    }

    public MissingParameterException(String message) {
        super(StatusCode.CTAP2_ERR_MISSING_PARAMETER, message);
    }

    public MissingParameterException(String message, Throwable cause) {
        super(StatusCode.CTAP2_ERR_MISSING_PARAMETER, message, cause);
    }

    public MissingParameterException(Throwable cause) {
        super(StatusCode.CTAP2_ERR_MISSING_PARAMETER, cause);
    }
}
