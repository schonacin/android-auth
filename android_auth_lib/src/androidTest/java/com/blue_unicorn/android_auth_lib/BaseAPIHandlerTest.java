package com.blue_unicorn.android_auth_lib;

import android.content.Context;
import android.util.Base64;

import androidx.test.platform.app.InstrumentationRegistry;

import com.blue_unicorn.android_auth_lib.api.APIHandler;
import com.blue_unicorn.android_auth_lib.api.BaseAPIHandler;
import com.blue_unicorn.android_auth_lib.api.authenticator.CredentialSafe;
import com.blue_unicorn.android_auth_lib.api.authenticator.database.PublicKeyCredentialSource;
import com.blue_unicorn.android_auth_lib.api.exceptions.InvalidOptionException;
import com.blue_unicorn.android_auth_lib.api.exceptions.NoCredentialsException;
import com.blue_unicorn.android_auth_lib.api.exceptions.OperationDeniedException;
import com.blue_unicorn.android_auth_lib.api.exceptions.UnsupportedAlgorithmException;
import com.blue_unicorn.android_auth_lib.cbor.BaseCborHandler;
import com.blue_unicorn.android_auth_lib.cbor.CborHandler;
import com.blue_unicorn.android_auth_lib.fido.reponse.GetAssertionResponse;
import com.blue_unicorn.android_auth_lib.fido.reponse.GetInfoResponse;
import com.blue_unicorn.android_auth_lib.fido.reponse.MakeCredentialResponse;
import com.blue_unicorn.android_auth_lib.fido.request.BaseGetInfoRequest;
import com.blue_unicorn.android_auth_lib.fido.request.GetAssertionRequest;
import com.blue_unicorn.android_auth_lib.fido.request.GetInfoRequest;
import com.blue_unicorn.android_auth_lib.fido.request.MakeCredentialRequest;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

public class BaseAPIHandlerTest {

    private static final byte[] RAW_MAKE_CREDENTIAL_REQUEST = Base64.decode("AaUBWCDMVG/Vi0CDgAtgnuEKnn1oM9yeVFNAkbx+pfadNopNfAKiYmlka3dlYmF1dGhuLmlvZG5hbWVrd2ViYXV0aG4uaW8Do2JpZEq3mQEAAAAAAAAAZG5hbWVkVXNlcmtkaXNwbGF5TmFtZWR1c2VyBIqiY2FsZyZkdHlwZWpwdWJsaWMta2V5omNhbGc4ImR0eXBlanB1YmxpYy1rZXmiY2FsZzgjZHR5cGVqcHVibGljLWtleaJjYWxnOQEAZHR5cGVqcHVibGljLWtleaJjYWxnOQEBZHR5cGVqcHVibGljLWtleaJjYWxnOQECZHR5cGVqcHVibGljLWtleaJjYWxnOCRkdHlwZWpwdWJsaWMta2V5omNhbGc4JWR0eXBlanB1YmxpYy1rZXmiY2FsZzgmZHR5cGVqcHVibGljLWtleaJjYWxnJ2R0eXBlanB1YmxpYy1rZXkFgA==", Base64.DEFAULT);

    private static final byte[] RAW_MAKE_CREDENTIAL_REQUEST_WITHOUT_SUPPORTED_ALGORITHM = Base64.decode("AaUBWCDMVG/Vi0CDgAtgnuEKnn1oM9yeVFNAkbx+pfadNopNfAKiYmlka3dlYmF1dGhuLmlvZG5hbWVrd2ViYXV0aG4uaW8Do2JpZEq3mQEAAAAAAAAAZG5hbWVkVXNlcmtkaXNwbGF5TmFtZWR1c2VyBImiY2FsZzgiZHR5cGVqcHVibGljLWtleaJjYWxnOCNkdHlwZWpwdWJsaWMta2V5omNhbGc5AQBkdHlwZWpwdWJsaWMta2V5omNhbGc5AQFkdHlwZWpwdWJsaWMta2V5omNhbGc5AQJkdHlwZWpwdWJsaWMta2V5omNhbGc4JGR0eXBlanB1YmxpYy1rZXmiY2FsZzglZHR5cGVqcHVibGljLWtleaJjYWxnOCZkdHlwZWpwdWJsaWMta2V5omNhbGcnZHR5cGVqcHVibGljLWtleQWA", Base64.DEFAULT);

    private static final byte[] RAW_MAKE_CREDENTIAL_REQUEST_WITH_INVALID_OPTION = Base64.decode("AaYBWCDMVG/Vi0CDgAtgnuEKnn1oM9yeVFNAkbx+pfadNopNfAKiYmlka3dlYmF1dGhuLmlvZG5hbWVrd2ViYXV0aG4uaW8Do2JpZEq3mQEAAAAAAAAAZG5hbWVkVXNlcmtkaXNwbGF5TmFtZWR1c2VyBIqiY2FsZyZkdHlwZWpwdWJsaWMta2V5omNhbGc4ImR0eXBlanB1YmxpYy1rZXmiY2FsZzgjZHR5cGVqcHVibGljLWtleaJjYWxnOQEAZHR5cGVqcHVibGljLWtleaJjYWxnOQEBZHR5cGVqcHVibGljLWtleaJjYWxnOQECZHR5cGVqcHVibGljLWtleaJjYWxnOCRkdHlwZWpwdWJsaWMta2V5omNhbGc4JWR0eXBlanB1YmxpYy1rZXmiY2FsZzgmZHR5cGVqcHVibGljLWtleaJjYWxnJ2R0eXBlanB1YmxpYy1rZXkFgAahZHBsYXT0", Base64.DEFAULT);

    private static final byte[] RAW_GET_ASSERTION_REQUEST = Base64.decode("AqQBa2V4YW1wbGUuY29tAlggaHE0loIi7BcgLkJQX47SsWriLxa7BbiMJdueYCZF8UEDgqJiaWRYQPIgBt5PkFr2ikOULwJPKl7OYD2cbUs9+L4I7QH8RCZG0DSFisdb7T/VgL+YCNlPy+6CubLvZnevCtzDWFLqa55kdHlwZWpwdWJsaWMta2V5omJpZFgyAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwNkdHlwZWpwdWJsaWMta2V5BaFidXb1", Base64.DEFAULT);

    private static final byte[] RAW_GET_ASSERTION_REQUEST_WITH_INVALID_OPTION = Base64.decode("AqQBa2V4YW1wbGUuY29tAlggaHE0loIi7BcgLkJQX47SsWriLxa7BbiMJdueYCZF8UEDgqJiaWRYQPIgBt5PkFr2ikOULwJPKl7OYD2cbUs9+L4I7QH8RCZG0DSFisdb7T/VgL+YCNlPy+6CubLvZnevCtzDWFLqa55kdHlwZWpwdWJsaWMta2V5omJpZFgyAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwNkdHlwZWpwdWJsaWMta2V5BaJidXb1ZHBsYXT0", Base64.DEFAULT);

    private CborHandler cborHandler;
    private APIHandler apiHandler;
    private CredentialSafe credentialSafe;

    @Before
    public void setUp() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        this.cborHandler = new BaseCborHandler();
        this.apiHandler = new BaseAPIHandler(context);
        this.credentialSafe = new CredentialSafe(context);

        resetKeystoreAndDatabase()
                .test()
                .assertComplete();
    }

    @After
    public void cleanUp() {
        resetKeystoreAndDatabase()
                .test()
                .assertComplete();
    }

    private Completable resetKeystoreAndDatabase() {
        return credentialSafe.getRxKeyStore().deleteAllEntries()
                .andThen(credentialSafe.deleteAllCredentials());
    }

    private Single<MakeCredentialRequest> initiateMakeCredential(byte[] request) {
        return cborHandler.decode(request)
                .flatMap(apiHandler::callAPI)
                .cast(MakeCredentialRequest.class);
    }

    private Single<MakeCredentialResponse> completeMakeCredential(byte[] request) {
        return initiateMakeCredential(request)
                .flatMap(req -> {
                    req.setApproved(true);
                    return Single.just(req);
                })
                .flatMap(apiHandler::updateAPI)
                .cast(MakeCredentialResponse.class);
    }

    @Test
    public void makeCredential_GoesThroughAPI() {
        completeMakeCredential(RAW_MAKE_CREDENTIAL_REQUEST)
                .test()
                .assertNoErrors()
                .assertValueCount(1);
    }

    @Test
    public void makeCredential_CreatesKeyPair() {
        completeMakeCredential(RAW_MAKE_CREDENTIAL_REQUEST)
                .flatMapPublisher(response -> credentialSafe.getRxKeyStore().getAliases())
                .test()
                .assertNoErrors()
                .assertValueCount(1);
    }

    @Test
    public void makeCredential_CreatesCredentialInDatabase() {
        PublicKeyCredentialSource credentialSource =
                completeMakeCredential(RAW_MAKE_CREDENTIAL_REQUEST)
                        .flatMapPublisher(response -> credentialSafe.getRxKeyStore().getAliases())
                        .flatMapSingle(alias -> credentialSafe.getCredentialSourceByAlias(alias))
                        .test()
                        .assertNoErrors()
                        .assertValueCount(1)
                        .values()
                        .get(0);

        assertThat(credentialSource.getUserDisplayName(), is("User"));
        assertThat(credentialSource.getRpId(), is("webauthn.io"));
    }

    @Test
    public void makeCredential_FailsWithoutUserApproval() {
        initiateMakeCredential(RAW_MAKE_CREDENTIAL_REQUEST)
                .flatMap(apiHandler::updateAPI)
                .test()
                .assertError(OperationDeniedException.class);
    }

    @Test
    public void makeCredential_FailsWithoutValidAlgorithm() {
        completeMakeCredential(RAW_MAKE_CREDENTIAL_REQUEST_WITHOUT_SUPPORTED_ALGORITHM)
                .test()
                .assertError(UnsupportedAlgorithmException.class);
    }

    @Test
    public void makeCredential_FailsWithInvalidOption() {
        initiateMakeCredential(RAW_MAKE_CREDENTIAL_REQUEST_WITH_INVALID_OPTION)
                .test()
                .assertError(InvalidOptionException.class);
    }

    private Single<GetAssertionRequest> initiateGetAssertion(byte[] request) {
        return cborHandler.decode(request)
                .flatMap(apiHandler::callAPI)
                .cast(GetAssertionRequest.class);
    }

    private Single<GetAssertionResponse> completeGetAssertion(byte[] request) {
        return initiateGetAssertion(request)
                .flatMap(req -> {
                    req.setApproved(true);
                    return Single.just(req);
                })
                .flatMap(apiHandler::updateAPI)
                .cast(GetAssertionResponse.class);
    }

    @Test
    public void getAssertion_throwsNoCredentialsExceptionIfNoCredentialsAreAvailable() {
        completeGetAssertion(RAW_GET_ASSERTION_REQUEST)
                .test()
                .assertError(NoCredentialsException.class);
    }

    @Test
    public void getAssertion_goesThroughAPI() {
        completeMakeCredential(RAW_MAKE_CREDENTIAL_REQUEST)
                .flatMap(res -> initiateGetAssertion(RAW_GET_ASSERTION_REQUEST))
                .flatMap(getAssertionRequest -> credentialSafe.getRxKeyStore().getAliases()
                        .flatMapSingle(this.credentialSafe::getCredentialSourceByAlias)
                        .toList()
                        .flatMap(credentials -> {
                            // small hack to emulate a request with correct credential
                            getAssertionRequest.setSelectedCredentials(credentials);
                            getAssertionRequest.setApproved(true);
                            return Single.just(getAssertionRequest);
                        }))
                .flatMap(apiHandler::updateAPI)
                .test()
                .assertNoErrors()
                .assertValueCount(1);
    }

    @Test
    public void getAssertion_FailsWithoutUserApproval() {
        initiateGetAssertion(RAW_GET_ASSERTION_REQUEST)
                .flatMap(apiHandler::updateAPI)
                .test()
                .assertError(OperationDeniedException.class);
    }

    @Test
    public void getAssertion_FailsWithInvalidOption() {
        initiateGetAssertion(RAW_GET_ASSERTION_REQUEST_WITH_INVALID_OPTION)
                .test()
                .assertError(InvalidOptionException.class);
    }

    private Single<GetInfoResponse> completeGetInfo() {
        GetInfoRequest request = new BaseGetInfoRequest();
        return apiHandler.callAPI(request)
                .cast(GetInfoResponse.class);
    }

    @Test
    public void getInfo_GoesThroughAPI() {
        completeGetInfo()
                .test()
                .assertNoErrors()
                .assertValueCount(1);
    }

}
