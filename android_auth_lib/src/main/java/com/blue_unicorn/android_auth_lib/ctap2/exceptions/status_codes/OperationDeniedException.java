package com.blue_unicorn.android_auth_lib.ctap2.exceptions.status_codes;

import com.blue_unicorn.android_auth_lib.ctap2.exceptions.StatusCodeException;

public class OperationDeniedException extends StatusCodeException {

    public OperationDeniedException() {
    }

    public OperationDeniedException(String message) {
        super(message);
    }

    public OperationDeniedException(String message, Throwable cause) {
        super(message, cause);
    }

    public OperationDeniedException(Throwable cause) {
        super(cause);
    }

}
