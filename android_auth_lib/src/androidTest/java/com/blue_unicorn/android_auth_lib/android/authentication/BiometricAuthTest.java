package com.blue_unicorn.android_auth_lib.android.authentication;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import com.blue_unicorn.android_auth_lib.android.BaseNotificationHandler;
import com.blue_unicorn.android_auth_lib.android.NotificationHandler;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class BiometricAuthTest {

    Context context;
    BiometricAuth biometricAuth;

    @Before
    public void setUp() {
        this.context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        this.biometricAuth = new BaseBiometricAuth();
    }

    @Ignore("WIP")
    @Test
    public void fingerPrint_DoesWork() {
        final boolean[] done = {false};
        biometricAuth.authenticate(context, new BaseAuthInfo("This is a Biometric Prompt", "RP Website: lol.de", "Userdata: me"), new AuthenticationAPICallback() {
            @Override
            public void handleAuthentication(boolean authenticated) {
                System.out.println(authenticated);
                done[0] = true;
            }
            @Override
            public void useFallback() {}
        });
        while (!done[0]) {

        }
    }

    //WIP
    @Ignore("WIP")
    @Test
    public void notificationsArePresent() {
        NotificationHandler notificationHandler = new BaseNotificationHandler(context,context.getClass());
        notificationHandler.requestApproval(new BaseAuthInfo("REGISTER!!!!", "haha.io", "me"), false);
    }

}
