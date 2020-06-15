package com.blue_unicorn.android_auth_lib.android.layers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Base64;

import androidx.lifecycle.MutableLiveData;
import androidx.test.platform.app.InstrumentationRegistry;

import com.blue_unicorn.android_auth_lib.android.AuthHandler;
import com.blue_unicorn.android_auth_lib.android.constants.UserAction;
import com.blue_unicorn.android_auth_lib.android.constants.UserPreference;

import org.junit.Before;
import org.junit.Test;

public class APILayerTest {

    private Context context;
    private AuthHandler authHandler;

    private static final byte[] RAW_MAKE_CREDENTIAL_REQUEST = Base64.decode("AaUBWCDMVG/Vi0CDgAtgnuEKnn1oM9yeVFNAkbx+pfadNopNfAKiYmlka3dlYmF1dGhuLmlvZG5hbWVrd2ViYXV0aG4uaW8Do2JpZEq3mQEAAAAAAAAAZG5hbWVkVXNlcmtkaXNwbGF5TmFtZWR1c2VyBIqiY2FsZyZkdHlwZWpwdWJsaWMta2V5omNhbGc4ImR0eXBlanB1YmxpYy1rZXmiY2FsZzgjZHR5cGVqcHVibGljLWtleaJjYWxnOQEAZHR5cGVqcHVibGljLWtleaJjYWxnOQEBZHR5cGVqcHVibGljLWtleaJjYWxnOQECZHR5cGVqcHVibGljLWtleaJjYWxnOCRkdHlwZWpwdWJsaWMta2V5omNhbGc4JWR0eXBlanB1YmxpYy1rZXmiY2FsZzgmZHR5cGVqcHVibGljLWtleaJjYWxnJ2R0eXBlanB1YmxpYy1rZXkFgA==", Base64.DEFAULT);

    @Before
    public void setUp() {
        this.context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        this.authHandler = new AuthHandler(context, new MutableLiveData<>());
        authHandler.setActivityClass(context.getClass());
    }

    private void setSharedPreferences(@UserPreference String preference, @UserAction int value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences
                .edit()
                .putInt(preference, value)
                .apply();
    }


    // not really real tests, just for my experiences
    @Test
    public void complexRequest_CallsBuildNotificationWhenSpecified() {
        setSharedPreferences(UserPreference.MAKE_CREDENTIAL, UserAction.BUILD_NOTIFICATION);
        authHandler.getApiLayer().buildNewRequestChain(RAW_MAKE_CREDENTIAL_REQUEST);
    }

    @Test
    public void complexRequest_DoesStandardAuthenticationWhenSpecified() {
        setSharedPreferences(UserPreference.MAKE_CREDENTIAL, UserAction.PERFORM_STANDARD_AUTHENTICATION);
        authHandler.getApiLayer().buildNewRequestChain(RAW_MAKE_CREDENTIAL_REQUEST);
    }

    @Test
    public void complexRequest_DoesCustomAuthentication() {
        setSharedPreferences(UserPreference.MAKE_CREDENTIAL, UserAction.PERFORM_AUTHENTICATION);
        //authHandler.getApiLayer().buildNewRequestChain(RAW_MAKE_CREDENTIAL_REQUEST);
    }

}
