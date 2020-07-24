package com.blue_unicorn.android_auth_lib.android.constants;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@StringDef({
        IntentAction.CTAP_PERFORM_AUTHENTICATION,
        IntentAction.CTAP_APPROVE_NOTIFICATION,
        IntentAction.CTAP_DECLINE_NOTIFICATION,
        IntentAction.CTAP_APPROVE_NOTIFICATION_FOR_AUTHENTICATION
})

public @interface IntentAction {
    String CTAP_PERFORM_AUTHENTICATION = "CTAP_PERFORM_AUTHENTICATION";
    String CTAP_APPROVE_NOTIFICATION = "CTAP_APPROVE_NOTIFICATION";
    String CTAP_DECLINE_NOTIFICATION = "CTAP_DECLINE_AUTHENTICATION";
    String CTAP_APPROVE_NOTIFICATION_FOR_AUTHENTICATION = "CTAP_APPROVE_NOTIFICATION_FOR_AUTHENTICATION";
    String CONTINUOS_AUTHENTICATION = "CONTINUOUS_AUTHENTICATION";
}
