package com.blue_unicorn.android_auth_lib.android.authentication;

import android.hardware.biometrics.BiometricPrompt;
import android.os.Build;

import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.P)
public abstract class AuthenticationAPICallback extends BiometricPrompt.AuthenticationCallback {

    public abstract void handleAuthentication(boolean authenticated);

    public abstract void useFallback();

    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString) {
        super.onAuthenticationError(errorCode, errString);
        useFallback();
    }

    @Override
    public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
        super.onAuthenticationSucceeded(result);
        handleAuthentication(true);
    }

    @Override
    public void onAuthenticationFailed() {
        super.onAuthenticationFailed();
        handleAuthentication(false);
    }

}
