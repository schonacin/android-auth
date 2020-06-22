package com.blue_unicorn.android_auth_lib.android.constants;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@IntDef({
        AuthenticationMethod.FINGERPRINT,
        AuthenticationMethod.FINGERPRINT_WITH_FALLBACK,
})

public @interface AuthenticationMethod {
    int FINGERPRINT = 1;
    int FINGERPRINT_WITH_FALLBACK = 2;
}
