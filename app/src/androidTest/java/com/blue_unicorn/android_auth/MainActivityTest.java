package com.blue_unicorn.android_auth;

import android.content.Context;
import android.content.Intent;
import android.util.Base64;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.blue_unicorn.android_auth_lib.android.AuthHandler;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.BleHandler;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.framing.BaseFragmentationProvider;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.framing.RxFragmentationProvider;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.framing.data.BaseFrame;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.framing.data.Fragment;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.framing.data.Frame;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    Context context;

    private RxFragmentationProvider fragmentationProvider;

    @Mock
    BleHandler bleHandler;

    @Captor
    ArgumentCaptor<byte[]> captor = ArgumentCaptor.forClass(byte[].class);

    @InjectMocks
    AuthHandler authHandler;

    private MainActivity mainActivity;

    @Rule
    public ActivityTestRule activityRule
            = new ActivityTestRule(
            MainActivity.class,
            true,     // initialTouchMode
            false) {
    };

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Intent intent = new Intent();
        mainActivity = (MainActivity) activityRule.launchActivity(intent);
        fragmentationProvider = new BaseFragmentationProvider();
    }

    private Observable<byte[]> RAW_MAKE_CREDENTIAL() {
        final byte[] RAW_REQUEST = Base64.decode("AaUBWCDMVG/Vi0CDgAtgnuEKnn1oM9yeVFNAkbx+pfadNopNfAKiYmlka3dlYmF1dGhuLmlvZG5hbWVrd2ViYXV0aG4uaW8Do2JpZEq3mQEAAAAAAAAAZG5hbWVkVXNlcmtkaXNwbGF5TmFtZWR1c2VyBIqiY2FsZyZkdHlwZWpwdWJsaWMta2V5omNhbGc4ImR0eXBlanB1YmxpYy1rZXmiY2FsZzgjZHR5cGVqcHVibGljLWtleaJjYWxnOQEAZHR5cGVqcHVibGljLWtleaJjYWxnOQEBZHR5cGVqcHVibGljLWtleaJjYWxnOQECZHR5cGVqcHVibGljLWtleaJjYWxnOCRkdHlwZWpwdWJsaWMta2V5omNhbGc4JWR0eXBlanB1YmxpYy1rZXmiY2FsZzgmZHR5cGVqcHVibGljLWtleaJjYWxnJ2R0eXBlanB1YmxpYy1rZXkFgA==", Base64.DEFAULT);
        return Single.fromCallable(() -> new BaseFrame(RAW_REQUEST))
                .cast(Frame.class)
                .flatMapPublisher(frame -> fragmentationProvider.fragment(Single.just(frame), 200))
                .map(Fragment::asBytes)
                .toObservable();
    }

    // These are also REAL LIFE tests where you need to interact with the phone.
    // Might get deleted later but helpful to test the app without Bluetooth Requirements
    // very hacky :D
    @Ignore("Reallife")
    @Test
    public void makeCredential_RunsFromActivity() {
        while (mainActivity.getFidoAuthService() == null) {
        }
        authHandler = mainActivity.getFidoAuthService().getAuthHandler();
        MockitoAnnotations.initMocks(this);

        when(bleHandler.getMtu()).thenReturn(200);
        when(bleHandler.getIncomingBleData()).thenReturn(RAW_MAKE_CREDENTIAL());
        mainActivity.getFidoAuthService().getAuthHandler().stopAdvertisingProcess();
        mainActivity.getFidoAuthService().getAuthHandler().startAdvertisingProcess();
        while (mainActivity.isBound()) {
        }

        verify(bleHandler, times(2)).sendBleData(captor.capture());
        List<byte[]> responses = captor.getAllValues();
    }

}
