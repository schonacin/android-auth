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

import io.reactivex.rxjava3.core.Observable;

class BaseAuthenticatorAPI implements AuthenticatorAPI{


    public Observable<MakeCredentialRequest> makeCredential(MakeCredentialRequest request) {

        return Observable.just(request);

    }

    public Observable<MakeCredentialResponse> makeInternalCredential(MakeCredentialRequest request) {

        return Observable.just(new BaseMakeCredentialResponse());

    }

    public Observable<GetAssertionRequest> getAssertion(GetAssertionRequest request) {

        return Observable.just(request);

    }

    public Observable<GetAssertionResponse> getInternalAssertion(GetAssertionRequest request) {

        return Observable.just(new BaseGetAssertionResponse());

    }

    public Observable<GetInfoResponse> getInfo(GetInfoRequest request) {

        return Observable.just(new BaseGetInfoResponse());

    }

}
