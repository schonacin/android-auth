package com.blue_unicorn.android_auth_lib.android.constants;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@StringDef({
        ExtensionValue.CONTINUOUS_AUTHENTICATION
})

public @interface ExtensionValue {
    String CONTINUOUS_AUTHENTICATION = "continuous_authentication";
}
