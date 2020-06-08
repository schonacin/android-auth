package com.blue_unicorn.android_auth_lib.android.authentication;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;

public class BiometricAuthTest {

    Context context;

    @Before
    public void setUp() {
        this.context = InstrumentationRegistry.getInstrumentation().getTargetContext();
    }

    @Test
    public void fingerPrint_DoesWork() {
        final boolean[] done = {false};
        BiometricAuth.authenticate(context, new AuthInfo("This is a Biometric Prompt", "RP Website: lol.de", "Userdata: me"), new AuthenticationAPICallback() {
            @Override
            public void handleAuthentication(boolean authenticated) {
                System.out.println(authenticated);
                done[0] = true;
            }
        });
        while (!done[0]) {

        }
    }

}
