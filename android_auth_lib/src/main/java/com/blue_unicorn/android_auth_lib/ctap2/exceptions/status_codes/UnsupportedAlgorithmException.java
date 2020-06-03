package com.blue_unicorn.android_auth_lib.ctap2.exceptions.status_codes;

import com.blue_unicorn.android_auth_lib.ctap2.constants.StatusCode;
import com.blue_unicorn.android_auth_lib.ctap2.exceptions.StatusCodeException;

public class UnsupportedAlgorithmException extends StatusCodeException {

    public UnsupportedAlgorithmException() {
        super(StatusCode.CTAP2_ERR_UNSUPPORTED_ALGORITHM);
    }

    public UnsupportedAlgorithmException(String message) {
        super(StatusCode.CTAP2_ERR_UNSUPPORTED_ALGORITHM, message);
    }

    public UnsupportedAlgorithmException(String message, Throwable cause) {
        super(StatusCode.CTAP2_ERR_UNSUPPORTED_ALGORITHM, message, cause);
    }

    public UnsupportedAlgorithmException(Throwable cause) {
        super(StatusCode.CTAP2_ERR_UNSUPPORTED_ALGORITHM, cause);
    }

}
