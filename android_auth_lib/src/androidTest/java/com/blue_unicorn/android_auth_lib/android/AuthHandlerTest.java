package com.blue_unicorn.android_auth_lib.android;

import android.content.Context;
import android.util.Base64;

import androidx.test.platform.app.InstrumentationRegistry;

import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.framing.BaseFragmentationProvider;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.framing.RxFragmentationProvider;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.framing.data.BaseFrame;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.framing.data.Fragment;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.framing.data.Frame;

import org.junit.Before;
import org.junit.Test;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.subscribers.TestSubscriber;

public class AuthHandlerTest {

    private Context context;

    private AuthHandler authHandler;
    private RxFragmentationProvider fragmentationProvider;

    private final static byte[] RAW_GET_INFO = new byte[]{(byte) 0x83, (byte) 0x00, (byte) 0x01, (byte) 0x04};

    @Before
    public void setUp() {
        this.context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        authHandler = new AuthHandler(context);
        fragmentationProvider = new BaseFragmentationProvider();
    }

    private Observable<byte[]> RAW_MAKE_CREDENTIAL() {
        final byte[] RAW_REQUEST = Base64.decode("AaUBWCDMVG/Vi0CDgAtgnuEKnn1oM9yeVFNAkbx+pfadNopNfAKiYmlka3dlYmF1dGhuLmlvZG5hbWVrd2ViYXV0aG4uaW8Do2JpZEq3mQEAAAAAAAAAZG5hbWVkVXNlcmtkaXNwbGF5TmFtZWR1c2VyBIqiY2FsZyZkdHlwZWpwdWJsaWMta2V5omNhbGc4ImR0eXBlanB1YmxpYy1rZXmiY2FsZzgjZHR5cGVqcHVibGljLWtleaJjYWxnOQEAZHR5cGVqcHVibGljLWtleaJjYWxnOQEBZHR5cGVqcHVibGljLWtleaJjYWxnOQECZHR5cGVqcHVibGljLWtleaJjYWxnOCRkdHlwZWpwdWJsaWMta2V5omNhbGc4JWR0eXBlanB1YmxpYy1rZXmiY2FsZzgmZHR5cGVqcHVibGljLWtleaJjYWxnJ2R0eXBlanB1YmxpYy1rZXkFgA==", Base64.DEFAULT);
        return Single.fromCallable(() -> new BaseFrame(RAW_REQUEST))
                .cast(Frame.class)
                .flatMapPublisher(frame -> fragmentationProvider.fragment(Single.just(frame), authHandler.getMaxLength()))
                .map(Fragment::asBytes)
                .toObservable();
    }

    @Test
    public void getInfo_runsThrough() {
        // TODO: maybe mock max length
        TestSubscriber<byte[]> subscriber = authHandler.getResponses().test();
        TestSubscriber<String> subscriber2 = authHandler.getResponses().map(bytes -> Base64.encodeToString(bytes, Base64.DEFAULT)).test();
        authHandler.initialize(Observable.just(RAW_GET_INFO));
        //List<byte[]> EXPECTED_RESPONSE = Arrays.asList(Base64.decode("gwA3AKQBgWhGSURPXzJfMANQAAA=", Base64.DEFAULT), Base64.decode("AAAAAAAAAAAAAAAAAAAABKRicms=", Base64.DEFAULT), Base64.decode("AfVidXD1YnV29WRwbGF09AUZBAA=", Base64.DEFAULT));
        subscriber
                .assertValueCount(3);
    }

    @Test
    public void makeCredential_runsThrough() {
        TestSubscriber<byte[]> subscriber = authHandler.getResponses().test();
        authHandler.initialize(RAW_MAKE_CREDENTIAL());
        subscriber.assertValueCount(0);
    }
}
