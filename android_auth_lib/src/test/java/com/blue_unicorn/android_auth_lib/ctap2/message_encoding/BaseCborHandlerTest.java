package com.blue_unicorn.android_auth_lib.ctap2.message_encoding;

import android.util.Base64;

import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.request.GetAssertionRequest;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.request.GetInfoRequest;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.request.MakeCredentialRequest;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.request.RequestObject;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.response.BaseGetAssertionResponse;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.response.BaseGetInfoResponse;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.response.BaseMakeCredentialResponse;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.response.GetAssertionResponse;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.response.GetInfoResponse;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.response.MakeCredentialResponse;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.webauthn.AttestationStatement;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.webauthn.BasePublicKeyCredentialDescriptor;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.webauthn.BasePublicKeyCredentialUserEntity;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.webauthn.PackedAttestationStatement;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.webauthn.PublicKeyCredentialDescriptor;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.webauthn.PublicKeyCredentialRpEntity;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.webauthn.PublicKeyCredentialUserEntity;
import com.blue_unicorn.android_auth_lib.ctap2.exceptions.status_codes.InvalidCommandException;
import com.blue_unicorn.android_auth_lib.ctap2.exceptions.status_codes.InvalidLengthException;
import com.blue_unicorn.android_auth_lib.ctap2.exceptions.status_codes.InvalidParameterException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.rxjava3.observers.TestObserver;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

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

    @Test
    public void getInfoResponse_transformsCorrectly() {
        Map<String, Boolean> options = new HashMap<>();
        options.putIfAbsent("rk", true);

        byte[] aaguid = Base64.decode("8dCxjhABSoFDQRyhBADx0A==", Base64.DEFAULT);
        int maxMsgSize = 1024;
        String[] versions = new String[]{"FIDO_2_0"};

        GetInfoResponse response = new BaseGetInfoResponse(versions, aaguid, options, maxMsgSize);

        byte[] encodedResponse =
                cborHandler.encode(response)
                        .test()
                        .assertNoErrors()
                        .assertComplete()
                        .assertValueCount(1)
                        .values()
                        .get(0);

        byte[] ENCODED_GET_INFO_RESPONSE = Base64.decode("AKQBgWhGSURPXzJfMANQ8dCxjhABSoFDQRyhBADx0AShYnJr9QUZBAA=", Base64.DEFAULT);

        String answer = Base64.encodeToString(encodedResponse, Base64.DEFAULT);

        assertThat(encodedResponse, is(ENCODED_GET_INFO_RESPONSE));
    }

    @Test
    public void makeCredentialResponse_transformsCorrectly() {
        byte[] authData = Base64.decode("EjRWeKvN71cSNFZ4q83vVxI0Vnirze9XEjRWeKvN71cSNFZ4q83vVxI0Vnirze9XEjRWeKvN71cSNFZ4q83vVxI0Vnirze9XEjRWeKvN71cSNFZ4q83vVxI0Vnirze9XEjRWeKvN71cSNFZ4q83vVxI0Vnirze9XEjRWeKvN71cSNFZ4q83vVxI0Vnir", Base64.DEFAULT); //141 bytes
        byte[] sig = Base64.decode("GhoaGhoaGhoaGhoaGhorKysrKys=", Base64.DEFAULT);
        AttestationStatement attStmt = new PackedAttestationStatement(sig);

        MakeCredentialResponse response = new BaseMakeCredentialResponse(authData, attStmt);

        byte[] encodedResponse =
                cborHandler.encode(response)
                        .test()
                        .assertNoErrors()
                        .assertComplete()
                        .assertValueCount(1)
                        .values()
                        .get(0);

        final byte[] ENCODED_MAKE_CREDENTIAL_RESPONSE = Base64.decode("AKMBZnBhY2tlZAJYjRI0Vnirze9XEjRWeKvN71cSNFZ4q83vVxI0Vnirze9XEjRWeKvN71cSNFZ4q83vVxI0Vnirze9XEjRWeKvN71cSNFZ4q83vVxI0Vnirze9XEjRWeKvN71cSNFZ4q83vVxI0Vnirze9XEjRWeKvN71cSNFZ4q83vVxI0Vnirze9XEjRWeKvN71cSNFZ4qwOiY2FsZyZjc2lnVBoaGhoaGhoaGhoaGhoaKysrKysr", Base64.DEFAULT);

        String answer = Base64.encodeToString(encodedResponse, Base64.DEFAULT);

        assertThat(encodedResponse, is(ENCODED_MAKE_CREDENTIAL_RESPONSE));
    }

    @Test
    public void getAssertionResponse_transformsCorrectly() {
        PublicKeyCredentialDescriptor credential = new BasePublicKeyCredentialDescriptor("public-key", Base64.decode("EjRWeJCrze8=", Base64.DEFAULT));
        byte[] authData = Base64.decode("EjRWeKvN71cSNFZ4q83vVxI0Vnirze9XEjRWeKvN71cSNFZ4q83vVxI0Vnirze9XEjRWeKvN71cSNFZ4q83vVxI0Vnirze9XEjRWeKvN71cSNFZ4q83vVxI0Vnirze9XEjRWeKvN71cSNFZ4q83vVxI0Vnirze9XEjRWeKvN71cSNFZ4q83vVxI0Vnir", Base64.DEFAULT); //141 bytes
        byte[] sig = Base64.decode("GhoaGhoaGhoaGhoaGhorKysrKys=", Base64.DEFAULT);

        GetAssertionResponse response = new BaseGetAssertionResponse(credential, authData, sig);

        PublicKeyCredentialUserEntity publicKeyCredentialUserEntity = new BasePublicKeyCredentialUserEntity(Base64.decode("EjRWeJCrze8SNFZ4kKvN7w==", Base64.DEFAULT));
        response.setPublicKeyCredentialUserEntity(publicKeyCredentialUserEntity);

        byte[] encodedResponse =
                cborHandler.encode(response)
                        .test()
                        .assertNoErrors()
                        .assertComplete()
                        .assertValueCount(1)
                        .values()
                        .get(0);

        final byte[] ENCODED_GET_ASSERTION_RESPONSE = Base64.decode("AKQBomJpZEgSNFZ4kKvN72R0eXBlanB1YmxpYy1rZXkCWI0SNFZ4q83vVxI0Vnirze9XEjRWeKvN71cSNFZ4q83vVxI0Vnirze9XEjRWeKvN71cSNFZ4q83vVxI0Vnirze9XEjRWeKvN71cSNFZ4q83vVxI0Vnirze9XEjRWeKvN71cSNFZ4q83vVxI0Vnirze9XEjRWeKvN71cSNFZ4q83vVxI0Vnirze9XEjRWeKsDVBoaGhoaGhoaGhoaGhoaKysrKysrBKFiaWRQEjRWeJCrze8SNFZ4kKvN7w==", Base64.DEFAULT);

        String answer = Base64.encodeToString(encodedResponse, Base64.DEFAULT);

        assertThat(encodedResponse, is(ENCODED_GET_ASSERTION_RESPONSE));
    }

}
