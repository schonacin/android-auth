package com.blue_unicorn.android_auth_lib.api;

import com.blue_unicorn.android_auth_lib.fido.GetAssertionRequest;
import com.blue_unicorn.android_auth_lib.fido.GetAssertionResponse;
import com.blue_unicorn.android_auth_lib.fido.GetInfoRequest;
import com.blue_unicorn.android_auth_lib.fido.GetInfoResponse;
import com.blue_unicorn.android_auth_lib.fido.MakeCredentialRequest;
import com.blue_unicorn.android_auth_lib.fido.MakeCredentialResponse;

import io.reactivex.rxjava3.core.Observable;

interface AuthenticatorAPI {

    Observable<MakeCredentialRequest> makeCredential(MakeCredentialRequest request);

    Observable<MakeCredentialResponse> makeInternalCredential(MakeCredentialRequest request);

    Observable<GetAssertionRequest> getAssertion(GetAssertionRequest request);

    Observable<GetAssertionResponse> getInternalAssertion(GetAssertionRequest request);

    Observable<GetInfoResponse> getInfo(GetInfoRequest request);

}
