package com.blue_unicorn.android_auth_lib.api;

import com.blue_unicorn.android_auth_lib.fido.GetAssertionRequest;
import com.blue_unicorn.android_auth_lib.fido.GetAssertionResponse;
import com.blue_unicorn.android_auth_lib.fido.GetInfoRequest;
import com.blue_unicorn.android_auth_lib.fido.GetInfoResponse;
import com.blue_unicorn.android_auth_lib.fido.MakeCredentialRequest;
import com.blue_unicorn.android_auth_lib.fido.MakeCredentialResponse;

import io.reactivex.rxjava3.core.Single;

interface AuthenticatorAPI {

    Single<MakeCredentialRequest> makeCredential(MakeCredentialRequest request);

    Single<MakeCredentialResponse> makeInternalCredential(MakeCredentialRequest request);

    Single<GetAssertionRequest> getAssertion(GetAssertionRequest request);

    Single<GetAssertionResponse> getInternalAssertion(GetAssertionRequest request);

    Single<GetInfoResponse> getInfo(GetInfoRequest request);

}
