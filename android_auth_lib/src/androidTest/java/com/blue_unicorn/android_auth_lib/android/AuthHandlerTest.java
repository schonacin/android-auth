package com.blue_unicorn.android_auth_lib.android;

import android.content.Context;
import android.util.Base64;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subscribers.TestSubscriber;

public class AuthHandlerTest {

    private AuthHandler authHandler;

    private final static byte[] RAW_GET_INFO = new byte[]{(byte)0x83, (byte)0x00, (byte)0x01, (byte)0x04};

    @Before
    public void setUp(){
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        authHandler = new AuthHandler(context);
    }

    @Test
    public void getInfo_runsThrough() {
        // TODO: maybe mock max length
        TestSubscriber<byte[]> subscriber = authHandler.getResponses().test();
        TestSubscriber<String> subscriber2 = authHandler.getResponses().map(bytes -> Base64.encodeToString(bytes, Base64.DEFAULT)).test();
        authHandler.startUp(Observable.just(RAW_GET_INFO));
        //List<byte[]> EXPECTED_RESPONSE = Arrays.asList(Base64.decode("gwA3AKQBgWhGSURPXzJfMANQAAA=", Base64.DEFAULT), Base64.decode("AAAAAAAAAAAAAAAAAAAABKRicms=", Base64.DEFAULT), Base64.decode("AfVidXD1YnV29WRwbGF09AUZBAA=", Base64.DEFAULT));
        subscriber
                .assertValueCount(3);
    }
}
