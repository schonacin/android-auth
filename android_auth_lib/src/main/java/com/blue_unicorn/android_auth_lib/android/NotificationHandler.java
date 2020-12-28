package com.blue_unicorn.android_auth_lib.android;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blue_unicorn.android_auth_lib.android.authentication.AuthInfo;
import com.blue_unicorn.android_auth_lib.android.constants.NotificationID;

/**
 * Handles the different notifications for showing success/ failure of an operation, requesting Approval of the user and that the Fido Service is active.
 */
public interface NotificationHandler {

    void notifyFailure();

    void notifyResult(@NonNull AuthInfo authInfo, boolean success);

    void notify(@NonNull String message);

    void requestApproval(AuthInfo authInfo, boolean authenticationRequired);

    void requestApproval(AuthInfo authInfo, boolean authenticationRequired, @Nullable Integer freshness);

    void showServiceActiveNotification(Context context);

    void closeNotification(@NotificationID int id);

    void closeAllNotifications();
}
