package com.blue_unicorn.android_auth_lib.ctap2.constants;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@IntDef({
        CommandValue.AUTHENTICATOR_MAKE_CREDENTIAL,
        CommandValue.AUTHENTICATOR_GET_ASSERTION,
        CommandValue.AUTHENTICATOR_GET_INFO
})
public @interface CommandValue {
    int AUTHENTICATOR_MAKE_CREDENTIAL = 0x01;
    int AUTHENTICATOR_GET_ASSERTION = 0x02;
    int AUTHENTICATOR_GET_INFO = 0x04;
}
