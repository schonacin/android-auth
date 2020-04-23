package com.blue_unicorn.android_auth_lib.ctap2.exceptions.status_codes;

import com.blue_unicorn.android_auth_lib.ctap2.exceptions.StatusCodeException;

public class CredentialExcludedException extends StatusCodeException {

    public CredentialExcludedException() {
    }

    public CredentialExcludedException(String message) {
        super(message);
    }

    public CredentialExcludedException(String message, Throwable cause) {
        super(message, cause);
    }

    public CredentialExcludedException(Throwable cause) {
        super(cause);
    }

}
