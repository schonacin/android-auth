package com.blue_unicorn.android_auth_lib.android.constants;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@IntDef({
        UserAction.BUILD_NOTIFICATION,
        UserAction.PERFORM_AUTHENTICATION,
        UserAction.BUILD_NOTIFICATION_AND_PERFORM_AUTHENTICATION,
        UserAction.PROCEED_WITHOUT_USER_INTERACTION
})
public @interface UserAction {
    int BUILD_NOTIFICATION = 1;
    int PERFORM_AUTHENTICATION = 2;
    int BUILD_NOTIFICATION_AND_PERFORM_AUTHENTICATION = 3;
    int PROCEED_WITHOUT_USER_INTERACTION = 4;
}
