package com.blue_unicorn.android_auth_lib.android.authentication;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.hardware.biometrics.BiometricPrompt;
import android.os.Build;
import android.os.CancellationSignal;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import java.util.Objects;
import java.util.concurrent.Executor;

import timber.log.Timber;

public final class BiometricAuth {
    // TODO test if device supports biometric prompts via hardware

    @RequiresApi(api = Build.VERSION_CODES.P)
    public static void authenticate(Context context, AuthInfo authInfo, AuthenticationAPICallback authenticationCallback) {
        Timber.d("Building Biometric Prompt for authentication...");
        Executor executor = ContextCompat.getMainExecutor(context);

        BiometricPrompt biometricPrompt = new BiometricPrompt.Builder(context)
                .setTitle(authInfo.getTitle())
                // TODO: username is not present in getAssertion
                .setSubtitle("Website: " + authInfo.getRp() + ", Username: " + authInfo.getUser())
                .setNegativeButton("Decline", executor, (dialog, position) -> authenticationCallback.onAuthenticationFailed())
                .build();

        // TODO do something with the cancellation signal
        biometricPrompt.authenticate(new CancellationSignal(), executor, authenticationCallback);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static void authenticateWithCredentialFallback(Context context, AuthInfo authInfo, AuthenticationAPICallback authenticationCallback) {
        Timber.d("Building Biometric Prompt for authentication with fallback...");
        Executor executor = ContextCompat.getMainExecutor(context);

        BiometricPrompt biometricPrompt = new BiometricPrompt.Builder(context)
                .setTitle(authInfo.getTitle())
                .setSubtitle("Website: " + authInfo.getRp() + ", Username: " + authInfo.getUser())
                .setDeviceCredentialAllowed(true)
                .build();

        biometricPrompt.authenticate(new CancellationSignal(), executor, authenticationCallback);
    }

    // will probably be deleted, doesn't work as intended because it expects a return intent -> only in activity possible :(
    public static void confirmCredentials(Context context, AuthInfo authInfo, Class activityClass) {
        Timber.d("Creating ConfirmDeviceCredentialIntent");
        String description = "Relying Party: " + authInfo.getRp() + ", User: " + authInfo.getUser();
        Intent intent = ((KeyguardManager) Objects.requireNonNull(context.getSystemService(Context.KEYGUARD_SERVICE))).createConfirmDeviceCredentialIntent(authInfo.getTitle(), description);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //intent.setClass(context, activityClass);
        context.startActivity(intent);
    }

}
