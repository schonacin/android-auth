package com.blue_unicorn.android_auth_lib.android;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Base64;

import androidx.lifecycle.MutableLiveData;
import androidx.test.platform.app.InstrumentationRegistry;

import com.blue_unicorn.android_auth_lib.android.constants.UserAction;
import com.blue_unicorn.android_auth_lib.android.constants.UserPreference;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.BleHandler;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.framing.BaseFragmentationProvider;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.framing.RxFragmentationProvider;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.framing.data.BaseFrame;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.framing.data.Fragment;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.framing.data.Frame;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AuthHandlerTest {

    private final static byte[] RAW_GET_INFO = new byte[]{(byte) 0x83, (byte) 0x00, (byte) 0x01, (byte) 0x04};

    private Context context;

    private RxFragmentationProvider fragmentationProvider;

    @Captor
    ArgumentCaptor<byte[]> captor = ArgumentCaptor.forClass(byte[].class);

    @Mock
    BleHandler bleHandler;

    @InjectMocks
    private AuthHandler authHandler;

    @Before
    public void setUp() {
        this.context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        authHandler = new AuthHandler(context, new MutableLiveData<>());
        fragmentationProvider = new BaseFragmentationProvider();
        MockitoAnnotations.initMocks(this);
    }

    private Observable<byte[]> RAW_MAKE_CREDENTIAL() {
        final byte[] RAW_REQUEST = Base64.decode("AaUBWCDMVG/Vi0CDgAtgnuEKnn1oM9yeVFNAkbx+pfadNopNfAKiYmlka3dlYmF1dGhuLmlvZG5hbWVrd2ViYXV0aG4uaW8Do2JpZEq3mQEAAAAAAAAAZG5hbWVkVXNlcmtkaXNwbGF5TmFtZWR1c2VyBIqiY2FsZyZkdHlwZWpwdWJsaWMta2V5omNhbGc4ImR0eXBlanB1YmxpYy1rZXmiY2FsZzgjZHR5cGVqcHVibGljLWtleaJjYWxnOQEAZHR5cGVqcHVibGljLWtleaJjYWxnOQEBZHR5cGVqcHVibGljLWtleaJjYWxnOQECZHR5cGVqcHVibGljLWtleaJjYWxnOCRkdHlwZWpwdWJsaWMta2V5omNhbGc4JWR0eXBlanB1YmxpYy1rZXmiY2FsZzgmZHR5cGVqcHVibGljLWtleaJjYWxnJ2R0eXBlanB1YmxpYy1rZXkFgA==", Base64.DEFAULT);
        return Single.fromCallable(() -> new BaseFrame(RAW_REQUEST))
                .cast(Frame.class)
                .flatMapPublisher(frame -> fragmentationProvider.fragment(Single.just(frame), 200))
                .map(Fragment::asBytes)
                .toObservable();
    }

    private void setSharedPreferences(@UserPreference String preference, @UserAction int value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences
                .edit()
                .putInt(preference, value)
                .apply();
    }

    @Test
    public void getInfo_runsThrough() {
        when(bleHandler.getMtu()).thenReturn(20);
        when(bleHandler.getIncomingBleData()).thenReturn(Observable.just(RAW_GET_INFO));

        authHandler.startAdvertisingProcess();

        verify(bleHandler, times(3)).sendBleData(captor.capture());
        List<byte[]> responses = captor.getAllValues();

        List<byte[]> EXPECTED_RESPONSE = Arrays.asList(Base64.decode("gwA3AKQBgWhGSURPXzJfMANQAAA=", Base64.DEFAULT), Base64.decode("AAAAAAAAAAAAAAAAAAAABKRicms=", Base64.DEFAULT), Base64.decode("AfVidXD1YnV29WRwbGF09AUZBAA=", Base64.DEFAULT));
        for (int i = 0; i < responses.size(); i++) {
            assertArrayEquals(EXPECTED_RESPONSE.get(0), responses.get(0));
        }
    }

    @Test
    public void makeCredential_runsThrough() {
        // TODO: mock specific method behaviour
        setSharedPreferences(UserPreference.MAKE_CREDENTIAL, UserAction.PROCEED_WITHOUT_USER_INTERACTION);

        when(bleHandler.getMtu()).thenReturn(200);
        when(bleHandler.getIncomingBleData()).thenReturn(RAW_MAKE_CREDENTIAL());

        authHandler.startAdvertisingProcess();

        verify(bleHandler, times(2)).sendBleData(captor.capture());
        List<byte[]> responses = captor.getAllValues();

        assertEquals(responses.size(), 2);
    }
}
