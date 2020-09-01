package com.blue_unicorn.android_auth_lib.android.authentication;

import android.content.Context;
import android.hardware.biometrics.BiometricPrompt;
import android.os.Build;
import android.os.CancellationSignal;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import java.util.concurrent.Executor;

import timber.log.Timber;

public class BaseBiometricAuth implements BiometricAuth {

    private CancellationSignal cancellationSignal;

    private static String getSubtitle(AuthInfo authInfo) {
        if (authInfo.getUser() == null) {
            return "Website: " + authInfo.getRp();
        } else {
            return "Website: " + authInfo.getRp() + ", Username: " + authInfo.getUser();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public void authenticate(Context context, AuthInfo authInfo, AuthenticationAPICallback authenticationCallback) {
        Timber.d("Building Biometric Prompt for authentication...");
        Executor executor = ContextCompat.getMainExecutor(context);

        BiometricPrompt biometricPrompt = new BiometricPrompt.Builder(context)
                .setTitle(authInfo.getTitle())
                .setSubtitle(getSubtitle(authInfo))
                .setNegativeButton("Decline", executor, (dialog, position) -> authenticationCallback.onAuthenticationFailed())
                .build();

        cancellationSignal = new CancellationSignal();
        biometricPrompt.authenticate(cancellationSignal, executor, authenticationCallback);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void authenticateWithCredentialFallback(Context context, AuthInfo authInfo, AuthenticationAPICallback authenticationCallback) {
        Timber.d("Building Biometric Prompt for authentication with fallback...");
        Executor executor = ContextCompat.getMainExecutor(context);

        BiometricPrompt biometricPrompt = new BiometricPrompt.Builder(context)
                .setTitle(authInfo.getTitle())
                .setSubtitle(getSubtitle(authInfo))
                .setDeviceCredentialAllowed(true)
                .build();

        cancellationSignal = new CancellationSignal();
        biometricPrompt.authenticate(cancellationSignal, executor, authenticationCallback);
    }

    public CancellationSignal getCancellationSignal() {
        return cancellationSignal;
    }

}
