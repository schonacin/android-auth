package com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.exceptions;

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
