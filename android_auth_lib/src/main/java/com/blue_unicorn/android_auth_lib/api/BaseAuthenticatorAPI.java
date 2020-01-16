package com.blue_unicorn.android_auth_lib.api;

import com.blue_unicorn.android_auth_lib.fido.BaseGetAssertionResponse;
import com.blue_unicorn.android_auth_lib.fido.BaseGetInfoResponse;
import com.blue_unicorn.android_auth_lib.fido.BaseMakeCredentialResponse;
import com.blue_unicorn.android_auth_lib.fido.GetAssertionRequest;
import com.blue_unicorn.android_auth_lib.fido.GetAssertionResponse;
import com.blue_unicorn.android_auth_lib.fido.GetInfoRequest;
import com.blue_unicorn.android_auth_lib.fido.GetInfoResponse;
import com.blue_unicorn.android_auth_lib.fido.MakeCredentialRequest;
import com.blue_unicorn.android_auth_lib.fido.MakeCredentialResponse;

import io.reactivex.rxjava3.core.Single;

class BaseAuthenticatorAPI implements AuthenticatorAPI{


    public Single<MakeCredentialRequest> makeCredential(MakeCredentialRequest request) {

        return Single.just(request);

    }

    public Single<MakeCredentialResponse> makeInternalCredential(MakeCredentialRequest request) {

        return Single.just(new BaseMakeCredentialResponse());

    }

    public Single<GetAssertionRequest> getAssertion(GetAssertionRequest request) {

        return Single.just(request);

    }

    public Single<GetAssertionResponse> getInternalAssertion(GetAssertionRequest request) {

        return Single.just(new BaseGetAssertionResponse());

    }

    public Single<GetInfoResponse> getInfo(GetInfoRequest request) {

        return Single.just(new BaseGetInfoResponse());

    }

}
