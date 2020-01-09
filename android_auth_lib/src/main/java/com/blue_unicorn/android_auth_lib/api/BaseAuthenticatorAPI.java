package com.blue_unicorn.android_auth_lib.api;

import com.blue_unicorn.android_auth_lib.fido.BaseGetAssertionRequest;
import com.blue_unicorn.android_auth_lib.fido.BaseGetAssertionResponse;
import com.blue_unicorn.android_auth_lib.fido.BaseGetInfoRequest;
import com.blue_unicorn.android_auth_lib.fido.BaseGetInfoResponse;
import com.blue_unicorn.android_auth_lib.fido.BaseMakeCredentialRequest;
import com.blue_unicorn.android_auth_lib.fido.BaseMakeCredentialResponse;

import io.reactivex.rxjava3.core.Observable;

class BaseAuthenticatorAPI implements AuthenticatorAPI{


    public Observable<BaseMakeCredentialRequest> makeCredential(BaseMakeCredentialRequest request) {

        return Observable.just(request);

    }

    public Observable<BaseMakeCredentialResponse> makeInternalCredential(BaseMakeCredentialRequest request) {

        return Observable.just(new BaseMakeCredentialResponse());

    }

    public Observable<BaseGetAssertionRequest> getAssertion(BaseGetAssertionRequest request) {

        return Observable.just(request);

    }

    public Observable<BaseGetAssertionResponse> getInternalAssertion(BaseGetAssertionRequest request) {

        return Observable.just(new BaseGetAssertionResponse());

    }

    public Observable<BaseGetInfoResponse> getInfo(BaseGetInfoRequest request) {

        return Observable.just(new BaseGetInfoResponse());

    }

}
