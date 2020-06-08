package com.blue_unicorn.android_auth_lib.ctap2.exceptions.status_codes;

import com.blue_unicorn.android_auth_lib.ctap2.constants.StatusCode;
import com.blue_unicorn.android_auth_lib.ctap2.exceptions.StatusCodeException;

public class OtherException extends StatusCodeException {

    public OtherException() {
        super(StatusCode.CTAP1_ERR_OTHER);
    }

    public OtherException(String message) {
        super(StatusCode.CTAP1_ERR_OTHER, message);
    }

    public OtherException(String message, Throwable cause) {
        super(StatusCode.CTAP1_ERR_OTHER, message, cause);
    }

    public OtherException(Throwable cause) {
        super(StatusCode.CTAP1_ERR_OTHER, cause);
    }
}
