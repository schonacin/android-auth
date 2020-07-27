package com.blue_unicorn.android_auth_lib.android.constants;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@IntDef({
        NotificationID.REQUEST,
        NotificationID.NOTIFY,
        NotificationID.SHOW_SERVICE
})

public @interface NotificationID {
    int REQUEST = 1;
    int NOTIFY = 2;
    int SHOW_SERVICE = 3;
}
