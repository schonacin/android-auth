package com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.exceptions;

import android.accounts.AuthenticatorException;

public class OperationDeniedException extends AuthenticatorException {

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
