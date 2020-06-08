package com.blue_unicorn.android_auth_lib.ctap2.exceptions.status_codes;

import com.blue_unicorn.android_auth_lib.ctap2.constants.StatusCode;
import com.blue_unicorn.android_auth_lib.ctap2.exceptions.StatusCodeException;

public class OperationDeniedException extends StatusCodeException {

    public OperationDeniedException() {
        super(StatusCode.CTAP2_ERR_OPERATION_DENIED);
    }

    public OperationDeniedException(String message) {
        super(StatusCode.CTAP2_ERR_OPERATION_DENIED, message);
    }

    public OperationDeniedException(String message, Throwable cause) {
        super(StatusCode.CTAP2_ERR_OPERATION_DENIED, message, cause);
    }

    public OperationDeniedException(Throwable cause) {
        super(StatusCode.CTAP2_ERR_OPERATION_DENIED, cause);
    }

}
