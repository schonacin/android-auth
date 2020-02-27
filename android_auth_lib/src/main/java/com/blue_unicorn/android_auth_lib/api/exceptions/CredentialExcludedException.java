package com.blue_unicorn.android_auth_lib.api.exceptions;

import android.accounts.AuthenticatorException;

public class CredentialExcludedException extends AuthenticatorException {

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