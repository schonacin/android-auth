package com.blue_unicorn.android_auth_lib.api.exceptions;

import android.accounts.AuthenticatorException;

public class NoCredentialsException extends AuthenticatorException {

    public NoCredentialsException() {
    }

    public NoCredentialsException(String message) {
        super(message);
    }

    public NoCredentialsException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoCredentialsException(Throwable cause) {
        super(cause);
    }
    
}
