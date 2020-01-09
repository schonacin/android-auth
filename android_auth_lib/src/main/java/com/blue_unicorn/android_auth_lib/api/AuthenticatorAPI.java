package com.blue_unicorn.android_auth_lib.api;

import com.blue_unicorn.android_auth_lib.fido.BaseGetAssertionRequest;
import com.blue_unicorn.android_auth_lib.fido.BaseGetAssertionResponse;
import com.blue_unicorn.android_auth_lib.fido.BaseGetInfoRequest;
import com.blue_unicorn.android_auth_lib.fido.BaseGetInfoResponse;
import com.blue_unicorn.android_auth_lib.fido.BaseMakeCredentialRequest;
import com.blue_unicorn.android_auth_lib.fido.BaseMakeCredentialResponse;

import io.reactivex.rxjava3.core.Observable;

interface AuthenticatorAPI {

    Observable<BaseMakeCredentialRequest> makeCredential(BaseMakeCredentialRequest request);

    Observable<BaseMakeCredentialResponse> makeInternalCredential(BaseMakeCredentialRequest request);

    Observable<BaseGetAssertionRequest> getAssertion(BaseGetAssertionRequest request);

    Observable<BaseGetAssertionResponse> getInternalAssertion(BaseGetAssertionRequest request);

    Observable<BaseGetInfoResponse> getInfo(BaseGetInfoRequest request);

}
