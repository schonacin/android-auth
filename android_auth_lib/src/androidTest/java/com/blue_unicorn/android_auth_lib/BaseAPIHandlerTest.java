package com.blue_unicorn.android_auth_lib;

import android.content.Context;
import android.util.Base64;

import androidx.test.platform.app.InstrumentationRegistry;

import com.blue_unicorn.android_auth_lib.api.APIHandler;
import com.blue_unicorn.android_auth_lib.api.BaseAPIHandler;
import com.blue_unicorn.android_auth_lib.api.authenticator.CredentialSafe;
import com.blue_unicorn.android_auth_lib.api.authenticator.database.PublicKeyCredentialSource;
import com.blue_unicorn.android_auth_lib.api.exceptions.NoCredentialsException;
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

    private static final byte[] RAW_GET_ASSERTION_REQUEST = Base64.decode("AqQBa2V4YW1wbGUuY29tAlggaHE0loIi7BcgLkJQX47SsWriLxa7BbiMJdueYCZF8UEDgqJiaWRYQPIgBt5PkFr2ikOULwJPKl7OYD2cbUs9+L4I7QH8RCZG0DSFisdb7T/VgL+YCNlPy+6CubLvZnevCtzDWFLqa55kdHlwZWpwdWJsaWMta2V5omJpZFgyAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwNkdHlwZWpwdWJsaWMta2V5BaFidXb1", Base64.DEFAULT);


    private Context context;

    private CborHandler cborHandler;
    private APIHandler apiHandler;
    private CredentialSafe credentialSafe;

    @Before
    public void setUp() {
        this.context = InstrumentationRegistry.getInstrumentation().getTargetContext();
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


    private Single<MakeCredentialResponse> completeMakeCredential() {
        return cborHandler.decode(RAW_MAKE_CREDENTIAL_REQUEST)
                .flatMap(apiHandler::callAPI)
                .cast(MakeCredentialRequest.class)
                .flatMap(req -> {
                    req.setApproved(true);
                    return Single.just(req);
                })
                .flatMap(apiHandler::updateAPI)
                .cast(MakeCredentialResponse.class);
    }

    @Test
    public void makeCredential_GoesThroughAPI() {
        completeMakeCredential()
            .test()
            .assertNoErrors()
            .assertValueCount(1);
    }

    @Test
    public void makeCredential_CreatesCredentialInDatabase() {
        PublicKeyCredentialSource credentialSource =
                completeMakeCredential()
                .flatMapPublisher(response -> credentialSafe.getRxKeyStore().getAliases())
                .firstOrError()
                .flatMap(alias -> credentialSafe.getCredentialSourceByAlias(alias))
                .test()
                .assertNoErrors()
                .assertValueCount(1)
                .values()
                .get(0);

        assertThat(credentialSource.userDisplayName, is("User"));
        assertThat(credentialSource.rpId, is("webauthn.io"));
    }

    private Single<GetAssertionResponse> completeGetAssertion() {
        return cborHandler.decode(RAW_GET_ASSERTION_REQUEST)
                .flatMap(apiHandler::callAPI)
                .cast(GetAssertionRequest.class)
                .flatMap(req -> {
                    req.setApproved(true);
                    return Single.just(req);
                })
                .flatMap(apiHandler::updateAPI)
                .cast(GetAssertionResponse.class);
    }

    @Test
    public void getAssertion_throwsNoCredentialsExceptionIfNoCredentialsAreAvailable() {
        completeGetAssertion()
                .test()
                .assertError(NoCredentialsException.class);
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
