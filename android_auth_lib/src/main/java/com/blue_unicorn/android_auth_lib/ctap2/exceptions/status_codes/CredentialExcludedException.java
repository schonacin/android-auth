package com.blue_unicorn.android_auth_lib.ctap2.exceptions.status_codes;

import com.blue_unicorn.android_auth_lib.ctap2.constants.StatusCode;
import com.blue_unicorn.android_auth_lib.ctap2.exceptions.StatusCodeException;

public class CredentialExcludedException extends StatusCodeException {

    public CredentialExcludedException() {
        super(StatusCode.CTAP2_ERR_CREDENTIAL_EXCLUDED);
    }

    public CredentialExcludedException(String message) {
        super(StatusCode.CTAP2_ERR_CREDENTIAL_EXCLUDED, message);
    }

    public CredentialExcludedException(String message, Throwable cause) {
        super(StatusCode.CTAP2_ERR_CREDENTIAL_EXCLUDED, message, cause);
    }

    public CredentialExcludedException(Throwable cause) {
        super(StatusCode.CTAP2_ERR_CREDENTIAL_EXCLUDED, cause);
    }

}
