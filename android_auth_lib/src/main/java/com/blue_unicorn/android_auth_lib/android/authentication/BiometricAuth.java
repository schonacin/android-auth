package com.blue_unicorn.android_auth_lib.android.authentication;

import android.content.Context;
import android.os.Build;
import android.os.CancellationSignal;

import androidx.annotation.RequiresApi;

/**
 * Handles the standard authentication mechanisms of the Phone like Fingerprint etc..
 */
public interface BiometricAuth {

    @RequiresApi(api = Build.VERSION_CODES.P)
    void authenticate(Context context, AuthInfo authInfo, AuthenticationAPICallback authenticationCallback);

    @RequiresApi(api = Build.VERSION_CODES.Q)
    void authenticateWithCredentialFallback(Context context, AuthInfo authInfo, AuthenticationAPICallback authenticationCallback);

    CancellationSignal getCancellationSignal();

}
