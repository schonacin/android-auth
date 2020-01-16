package com.blue_unicorn.android_auth_lib;

import com.blue_unicorn.android_auth_lib.cbor.BaseCborHandler;
import com.blue_unicorn.android_auth_lib.cbor.CborHandler;
import com.blue_unicorn.android_auth_lib.exception.InvalidCommandException;
import com.blue_unicorn.android_auth_lib.exception.InvalidLengthException;
import com.blue_unicorn.android_auth_lib.exception.InvalidParameterException;
import com.blue_unicorn.android_auth_lib.fido.GetAssertionRequest;
import com.blue_unicorn.android_auth_lib.fido.GetInfoRequest;
import com.blue_unicorn.android_auth_lib.fido.MakeCredentialRequest;
import com.blue_unicorn.android_auth_lib.fido.PublicKeyCredentialRpEntity;
import com.blue_unicorn.android_auth_lib.fido.PublicKeyCredentialUserEntity;
import com.blue_unicorn.android_auth_lib.fido.RequestObject;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.apache.commons.codec.binary.Base64;
import org.junit.Before;
import org.junit.Test;

import io.reactivex.rxjava3.observers.TestObserver;

public class BaseCborHandlerTest {

    private static final byte[] RAW_MAKE_CREDENTIAL_REQUEST = Base64.decodeBase64("AaUBWCDMVG/Vi0CDgAtgnuEKnn1oM9yeVFNAkbx+pfadNopNfAKiYmlka3dlYmF1dGhuLmlvZG5hbWVrd2ViYXV0aG4uaW8Do2JpZEq3mQEAAAAAAAAAZG5hbWVkVXNlcmtkaXNwbGF5TmFtZWR1c2VyBIqiY2FsZyZkdHlwZWpwdWJsaWMta2V5omNhbGc4ImR0eXBlanB1YmxpYy1rZXmiY2FsZzgjZHR5cGVqcHVibGljLWtleaJjYWxnOQEAZHR5cGVqcHVibGljLWtleaJjYWxnOQEBZHR5cGVqcHVibGljLWtleaJjYWxnOQECZHR5cGVqcHVibGljLWtleaJjYWxnOCRkdHlwZWpwdWJsaWMta2V5omNhbGc4JWR0eXBlanB1YmxpYy1rZXmiY2FsZzgmZHR5cGVqcHVibGljLWtleaJjYWxnJ2R0eXBlanB1YmxpYy1rZXkFgA==");

    private static final byte[] RAW_GET_ASSERTION_REQUEST = Base64.decodeBase64("AqQBa2V4YW1wbGUuY29tAlggaHE0loIi7BcgLkJQX47SsWriLxa7BbiMJdueYCZF8UEDgqJiaWRYQPIgBt5PkFr2ikOULwJPKl7OYD2cbUs9+L4I7QH8RCZG0DSFisdb7T/VgL+YCNlPy+6CubLvZnevCtzDWFLqa55kdHlwZWpwdWJsaWMta2V5omJpZFgyAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwNkdHlwZWpwdWJsaWMta2V5BaFidXb1");

    private static final byte[] RAW_GET_INFO_REQUEST = Base64.decodeBase64("BA==");

    private static final byte[] RAW_INVALID_CMD_REQUEST = Base64.decodeBase64("/w==");

    private static final byte[] RAW_EMPTY_REQUEST = new byte[]{};

    private static final byte[] RAW_INVALID_PARAMETER_REQUEST = Base64.decodeBase64("AXc=");

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

        testObserver
                .assertValueCount(1);

        assertThat(testObserver.values().get(0), is(instanceOf(MakeCredentialRequest.class)));

        MakeCredentialRequest transformedMakeCredentialRequest =
                (MakeCredentialRequest) testObserver
                        .values().get(0);

        final byte[] CLIENT_DATA_HASH = Base64.decodeBase64("zFRv1YtAg4ALYJ7hCp59aDPcnlRTQJG8fqX2nTaKTXw=");
        assertThat(transformedMakeCredentialRequest.getClientDataHash(), is(CLIENT_DATA_HASH));

        PublicKeyCredentialUserEntity user = transformedMakeCredentialRequest.getUser();
        assertThat(user.getName(), is("User"));
        assertThat(user.getDisplayName(), is("user"));
        assertThat(user.getId(), is(new byte[]{-73, -103, 1, 0, 0, 0, 0, 0, 0 ,0}));

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

        testObserver
                .assertValueCount(1);

        assertThat(testObserver.values().get(0), is(instanceOf(GetAssertionRequest.class)));

        GetAssertionRequest transformedGetAssertionRequest =
                (GetAssertionRequest) testObserver
                        .values()
                        .get(0);

        final byte[] CLIENT_DATA_HASH = Base64.decodeBase64("aHE0loIi7BcgLkJQX47SsWriLxa7BbiMJdueYCZF8UE=");

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

        testObserver
                .assertValueCount(1);

        assertThat(testObserver.values().get(0), is(instanceOf(GetInfoRequest.class)));

    }

    @Test
    public void invalidCmdRequest_fails() {
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
    public void invalidParRequest_fails() {
        cborHandler.decode(RAW_INVALID_PARAMETER_REQUEST)
                .test()
                .assertError(InvalidParameterException.class);
    }

}
