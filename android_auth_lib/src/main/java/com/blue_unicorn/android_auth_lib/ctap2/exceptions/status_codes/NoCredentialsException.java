package com.blue_unicorn.android_auth_lib.ctap2.exceptions.status_codes;

import com.blue_unicorn.android_auth_lib.ctap2.constants.StatusCode;
import com.blue_unicorn.android_auth_lib.ctap2.exceptions.StatusCodeException;

public class NoCredentialsException extends StatusCodeException {

    public NoCredentialsException() {
        super(StatusCode.CTAP2_ERR_NO_CREDENTIALS);
    }

    public NoCredentialsException(String message) {
        super(StatusCode.CTAP2_ERR_NO_CREDENTIALS, message);
    }

    public NoCredentialsException(String message, Throwable cause) {
        super(StatusCode.CTAP2_ERR_NO_CREDENTIALS, message, cause);
    }

    public NoCredentialsException(Throwable cause) {
        super(StatusCode.CTAP2_ERR_NO_CREDENTIALS, cause);
    }

}
