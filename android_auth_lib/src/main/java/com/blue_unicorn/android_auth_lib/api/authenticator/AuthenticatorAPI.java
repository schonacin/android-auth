package com.blue_unicorn.android_auth_lib.api.authenticator;

import com.blue_unicorn.android_auth_lib.fido.request.GetAssertionRequest;
import com.blue_unicorn.android_auth_lib.fido.reponse.GetAssertionResponse;
import com.blue_unicorn.android_auth_lib.fido.request.GetInfoRequest;
import com.blue_unicorn.android_auth_lib.fido.reponse.GetInfoResponse;
import com.blue_unicorn.android_auth_lib.fido.request.MakeCredentialRequest;
import com.blue_unicorn.android_auth_lib.fido.reponse.MakeCredentialResponse;

import io.reactivex.rxjava3.core.Single;

public interface AuthenticatorAPI {

    Single<MakeCredentialRequest> makeCredential(MakeCredentialRequest request);

    Single<MakeCredentialResponse> makeInternalCredential(MakeCredentialRequest request);

    Single<GetAssertionRequest> getAssertion(GetAssertionRequest request);

    Single<GetAssertionResponse> getInternalAssertion(GetAssertionRequest request);

    Single<GetInfoResponse> getInfo(GetInfoRequest request);

}
