package com.blue_unicorn.android_auth_lib.android;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;

import io.reactivex.rxjava3.core.Observable;

public class AuthHandlerTest {

    private AuthHandler authHandler;

    private final static byte[] RAW_GET_INFO = new byte[]{(byte)0x83, (byte)0x00, (byte)0x01, (byte)0x04};

    @Before
    public void setUp(){
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        authHandler = new AuthHandler(context);
    }

    @Test
    public void getInfoRunsThrough() {
        authHandler.startUp(Observable.just(RAW_GET_INFO));
    }
}
