package com.blue_unicorn.android_auth_lib.api;

import android.content.Context;

import com.blue_unicorn.android_auth_lib.api.authenticator.CredentialSafe;
import com.blue_unicorn.android_auth_lib.api.authenticator.GetAssertion;
import com.blue_unicorn.android_auth_lib.api.authenticator.GetInfo;
import com.blue_unicorn.android_auth_lib.api.authenticator.MakeCredential;
import com.blue_unicorn.android_auth_lib.fido.request.GetAssertionRequest;
import com.blue_unicorn.android_auth_lib.fido.reponse.GetAssertionResponse;
import com.blue_unicorn.android_auth_lib.fido.request.GetInfoRequest;
import com.blue_unicorn.android_auth_lib.fido.reponse.GetInfoResponse;
import com.blue_unicorn.android_auth_lib.fido.request.MakeCredentialRequest;
import com.blue_unicorn.android_auth_lib.fido.reponse.MakeCredentialResponse;

import io.reactivex.rxjava3.core.Single;

public class BaseAuthenticatorAPI implements AuthenticatorAPI{

    private CredentialSafe credentialSafe;

    public BaseAuthenticatorAPI(Context ctx, boolean authenticationRequired) {
        this.credentialSafe = new CredentialSafe(ctx, authenticationRequired);
    }

    public Single<MakeCredentialRequest> makeCredential(MakeCredentialRequest request) {
        return Single.defer(() -> {
            MakeCredential makeCredential = new MakeCredential(credentialSafe, request);
            return makeCredential.operate();
        });
    }

    public Single<MakeCredentialResponse> makeInternalCredential(MakeCredentialRequest request) {
        return Single.defer(() -> {
            MakeCredential makeCredential = new MakeCredential(credentialSafe, request);
            return makeCredential.operateInner();
        });
    }

    public Single<GetAssertionRequest> getAssertion(GetAssertionRequest request) {
        return Single.defer(() -> {
            GetAssertion getAssertion = new GetAssertion(credentialSafe, request);
            return getAssertion.operate();
        });
    }

    public Single<GetAssertionResponse> getInternalAssertion(GetAssertionRequest request) {
        return Single.defer(() -> {
            GetAssertion getAssertion = new GetAssertion(credentialSafe, request);
            return getAssertion.operateInner();
        });
    }

    public Single<GetInfoResponse> getInfo(GetInfoRequest request) {
        return Single.defer(() -> {
            GetInfo getInfo = new GetInfo();
            return getInfo.operate();
        });
    }

}
