package com.blue_unicorn.android_auth_lib.android.constants;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@IntDef({
        AuthenticatorAPIMethod.MAKE_CREDENTIAL,
        AuthenticatorAPIMethod.GET_ASSERTION
})

public @interface AuthenticatorAPIMethod {
    int MAKE_CREDENTIAL = 1;
    int GET_ASSERTION = 2;
}
