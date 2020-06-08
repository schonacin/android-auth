package com.blue_unicorn.android_auth_lib.ctap2.constants;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@IntDef({
        StatusCode.CTAP2_OK,
        StatusCode.CTAP1_ERR_INVALID_COMMAND,
        StatusCode.CTAP1_ERR_INVALID_PARAMETER,
        StatusCode.CTAP1_ERR_INVALID_LENGTH,
        StatusCode.CTAP2_ERR_MISSING_PARAMETER,
        StatusCode.CTAP2_ERR_CREDENTIAL_EXCLUDED,
        StatusCode.CTAP2_ERR_UNSUPPORTED_ALGORITHM,
        StatusCode.CTAP2_ERR_OPERATION_DENIED,
        StatusCode.CTAP2_ERR_INVALID_OPTION,
        StatusCode.CTAP2_ERR_NO_CREDENTIALS,
        StatusCode.CTAP1_ERR_OTHER
})
public @interface StatusCode {
    int CTAP2_OK = 0x00;
    int CTAP1_ERR_INVALID_COMMAND = 0x01;
    int CTAP1_ERR_INVALID_PARAMETER = 0x02;
    int CTAP1_ERR_INVALID_LENGTH = 0x03;
    int CTAP2_ERR_MISSING_PARAMETER = 0x14;
    int CTAP2_ERR_CREDENTIAL_EXCLUDED = 0x19;
    int CTAP2_ERR_UNSUPPORTED_ALGORITHM = 0x26;
    int CTAP2_ERR_OPERATION_DENIED = 0x27;
    int CTAP2_ERR_INVALID_OPTION = 0x2C;
    int CTAP2_ERR_NO_CREDENTIALS = 0x2E;
    int CTAP1_ERR_OTHER = 0x7F;
}
