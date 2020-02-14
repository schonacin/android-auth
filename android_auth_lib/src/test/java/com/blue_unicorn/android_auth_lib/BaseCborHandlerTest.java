package com.blue_unicorn.android_auth_lib;

import android.util.Base64;

import com.blue_unicorn.android_auth_lib.ctap2.message_encoding.BaseCborHandler;
import com.blue_unicorn.android_auth_lib.ctap2.message_encoding.CborHandler;
import com.blue_unicorn.android_auth_lib.ctap2.exceptions.InvalidCommandException;
import com.blue_unicorn.android_auth_lib.ctap2.exceptions.InvalidLengthException;
import com.blue_unicorn.android_auth_lib.ctap2.exceptions.InvalidParameterException;
import com.blue_unicorn.android_auth_lib.ctap2.data.GetAssertionRequest;
import com.blue_unicorn.android_auth_lib.ctap2.data.GetInfoRequest;
import com.blue_unicorn.android_auth_lib.ctap2.data.MakeCredentialRequest;
import com.blue_unicorn.android_auth_lib.ctap2.data.PublicKeyCredentialRpEntity;
import com.blue_unicorn.android_auth_lib.ctap2.data.PublicKeyCredentialUserEntity;
import com.blue_unicorn.android_auth_lib.ctap2.data.RequestObject;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import io.reactivex.rxjava3.observers.TestObserver;

@RunWith(RobolectricTestRunner.class)
public class BaseCborHandlerTest {

    private static final byte[] RAW_MAKE_CREDENTIAL_REQUEST = Base64.decode("AaUBWCDMVG/Vi0CDgAtgnuEKnn1oM9yeVFNAkbx+pfadNopNfAKiYmlka3dlYmF1dGhuLmlvZG5hbWVrd2ViYXV0aG4uaW8Do2JpZEq3mQEAAAAAAAAAZG5hbWVkVXNlcmtkaXNwbGF5TmFtZWR1c2VyBIqiY2FsZyZkdHlwZWpwdWJsaWMta2V5omNhbGc4ImR0eXBlanB1YmxpYy1rZXmiY2FsZzgjZHR5cGVqcHVibGljLWtleaJjYWxnOQEAZHR5cGVqcHVibGljLWtleaJjYWxnOQEBZHR5cGVqcHVibGljLWtleaJjYWxnOQECZHR5cGVqcHVibGljLWtleaJjYWxnOCRkdHlwZWpwdWJsaWMta2V5omNhbGc4JWR0eXBlanB1YmxpYy1rZXmiY2FsZzgmZHR5cGVqcHVibGljLWtleaJjYWxnJ2R0eXBlanB1YmxpYy1rZXkFgA==", Base64.DEFAULT);

    private static final byte[] RAW_GET_ASSERTION_REQUEST = Base64.decode("AqQBa2V4YW1wbGUuY29tAlggaHE0loIi7BcgLkJQX47SsWriLxa7BbiMJdueYCZF8UEDgqJiaWRYQPIgBt5PkFr2ikOULwJPKl7OYD2cbUs9+L4I7QH8RCZG0DSFisdb7T/VgL+YCNlPy+6CubLvZnevCtzDWFLqa55kdHlwZWpwdWJsaWMta2V5omJpZFgyAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwNkdHlwZWpwdWJsaWMta2V5BaFidXb1", Base64.DEFAULT);

    private static final byte[] RAW_GET_INFO_REQUEST = new byte[]{(byte) 0x4};

    private static final byte[] RAW_INVALID_CMD_REQUEST = new byte[]{(byte) 0xff};

    private static final byte[] RAW_EMPTY_REQUEST = new byte[]{};

    private static final byte[] RAW_INVALID_PARAMETER_REQUEST = new byte[]{(byte) 0x1, (byte) 0x77};

    private CborHandler cborHandler;

    @Before
    public void setUp() {
        cborHandler = new BaseCborHandler();
    }

    @Test
    public void makeCredentialRequest_transformsWithNoErrors() {
        cborHandler.decode(RAW_MAKE_CREDENTIAL_REQUEST)
                .test()
                .assertNoErrors()
                .assertComplete();
    }

    @Test
    public void makeCredentialRequest_transformsWithRightValues() {
        TestObserver<RequestObject> testObserver = cborHandler.decode(RAW_MAKE_CREDENTIAL_REQUEST)
                .test();

        testObserver.assertValueCount(1);

        assertThat(testObserver.values().get(0), is(instanceOf(MakeCredentialRequest.class)));

        MakeCredentialRequest transformedMakeCredentialRequest =
                (MakeCredentialRequest) testObserver.values()
                        .get(0);

        final byte[] CLIENT_DATA_HASH = Base64.decode("zFRv1YtAg4ALYJ7hCp59aDPcnlRTQJG8fqX2nTaKTXw=", Base64.DEFAULT);
        assertThat(transformedMakeCredentialRequest.getClientDataHash(), is(CLIENT_DATA_HASH));

        PublicKeyCredentialUserEntity user = transformedMakeCredentialRequest.getUser();
        assertThat(user.getName(), is("User"));
        assertThat(user.getDisplayName(), is("user"));
        assertThat(user.getId(), is(new byte[]{-73, -103, 1, 0, 0, 0, 0, 0, 0, 0}));

        PublicKeyCredentialRpEntity rp = transformedMakeCredentialRequest.getRp();
        assertThat(rp.getId(), is("webauthn.io"));
        assertThat(rp.getName(), is("webauthn.io"));

        assertThat(transformedMakeCredentialRequest.getPubKeyCredParams().length, is(10));
        assertThat(transformedMakeCredentialRequest.getExcludeList().size(), is(0));
    }

    @Test
    public void getAssertionRequest_transformsWithNoErrors() {
        cborHandler.decode(RAW_GET_ASSERTION_REQUEST)
                .test()
                .assertNoErrors()
                .assertComplete();
    }

    @Test
    public void getAssertionRequest_transformsWithRightValues() {
        TestObserver testObserver = cborHandler.decode(RAW_GET_ASSERTION_REQUEST)
                .test();

        testObserver.assertValueCount(1);

        assertThat(testObserver.values().get(0), is(instanceOf(GetAssertionRequest.class)));

        GetAssertionRequest transformedGetAssertionRequest =
                (GetAssertionRequest) testObserver.values()
                        .get(0);

        final byte[] CLIENT_DATA_HASH = Base64.decode("aHE0loIi7BcgLkJQX47SsWriLxa7BbiMJdueYCZF8UE=", Base64.DEFAULT);
        assertThat(transformedGetAssertionRequest.getClientDataHash(), is(CLIENT_DATA_HASH));

        assertThat(transformedGetAssertionRequest.getRpId(), is("example.com"));
        assertThat(transformedGetAssertionRequest.getAllowList().size(), is(2));
        assertThat(transformedGetAssertionRequest.getOptions().size(), is(1));
    }

    @Test
    public void getInfoRequest_transformsWithNoErrors() {
        cborHandler.decode(RAW_GET_INFO_REQUEST)
                .test()
                .assertNoErrors()
                .assertComplete();
    }

    @Test
    public void getInfoRequest_transformsWithRightValues() {
        TestObserver testObserver = cborHandler.decode(RAW_GET_INFO_REQUEST)
                .test();

        testObserver.assertValueCount(1);

        assertThat(testObserver.values().get(0), is(instanceOf(GetInfoRequest.class)));
    }

    @Test
    public void invalidCommandRequest_fails() {
        cborHandler.decode(RAW_INVALID_CMD_REQUEST)
                .test()
                .assertError(InvalidCommandException.class);
    }

    @Test
    public void emptyRequest_fails() {
        cborHandler.decode(RAW_EMPTY_REQUEST)
                .test()
                .assertError(InvalidLengthException.class);
    }

    @Test
    public void invalidParameterRequest_fails() {
        cborHandler.decode(RAW_INVALID_PARAMETER_REQUEST)
                .test()
                .assertError(InvalidParameterException.class);
    }

}
