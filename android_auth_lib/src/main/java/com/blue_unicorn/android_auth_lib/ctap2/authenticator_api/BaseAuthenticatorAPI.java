package com.blue_unicorn.android_auth_lib.ctap2.authenticator_api;

import android.content.Context;

import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.authenticator.CredentialSafe;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.authenticator.GetAssertion;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.authenticator.GetInfo;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.authenticator.MakeCredential;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.request.GetAssertionRequest;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.request.GetInfoRequest;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.request.MakeCredentialRequest;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.response.GetAssertionResponse;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.response.GetInfoResponse;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.response.MakeCredentialResponse;

import io.reactivex.rxjava3.core.Single;

public class BaseAuthenticatorAPI implements AuthenticatorAPI {

    private CredentialSafe credentialSafe;

    public BaseAuthenticatorAPI(Context context, boolean authenticationRequired) {
        this.credentialSafe = new CredentialSafe(context, authenticationRequired);
    }

    public Single<MakeCredentialRequest> makeCredential(MakeCredentialRequest request) {
        return Single.fromCallable(() -> new MakeCredential(credentialSafe, request))
                .flatMap(MakeCredential::operate);
    }

    public Single<MakeCredentialResponse> makeInternalCredential(MakeCredentialRequest request) {
        return Single.fromCallable(() -> new MakeCredential(credentialSafe, request))
                .flatMap(MakeCredential::operateInner);
    }

    public Single<GetAssertionRequest> getAssertion(GetAssertionRequest request) {
        return Single.fromCallable(() -> new GetAssertion(credentialSafe, request))
                .flatMap(GetAssertion::operate);
    }

    public Single<GetAssertionResponse> getInternalAssertion(GetAssertionRequest request) {
        return Single.fromCallable(() -> new GetAssertion(credentialSafe, request))
                .flatMap(GetAssertion::operateInner);
    }

    public Single<GetInfoResponse> getInfo(GetInfoRequest request) {
        return Single.fromCallable(GetInfo::new)
                .flatMap(GetInfo::operate);
    }

}
