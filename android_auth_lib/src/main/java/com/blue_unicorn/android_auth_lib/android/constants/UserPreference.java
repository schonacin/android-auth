package com.blue_unicorn.android_auth_lib.android.constants;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@StringDef({
        UserPreference.MAKE_CREDENTIAL,
        UserPreference.GET_ASSERTION
})

public @interface UserPreference {
    String MAKE_CREDENTIAL = "MAKE_CREDENTIAL_ACTION";
    String GET_ASSERTION = "GET_ASSERTION_ACTION";
}
