package com.blue_unicorn.android_auth_lib.android.constants;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@StringDef({
        IntentExtra.ACTIVITY_CLASS,
        IntentExtra.AUTHENTICATION_FRESHNESS
})

public @interface IntentExtra {
    String ACTIVITY_CLASS = "ACTIVITY_CLASS";
    String AUTHENTICATION_FRESHNESS = "AUTHENTICATION_FRESHNESS";
}
