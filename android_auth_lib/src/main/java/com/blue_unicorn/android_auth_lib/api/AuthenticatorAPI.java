package com.blue_unicorn.android_auth_lib.api;

import com.blue_unicorn.android_auth_lib.fido.reponse.GetAssertionResponse;
import com.blue_unicorn.android_auth_lib.fido.reponse.GetInfoResponse;
import com.blue_unicorn.android_auth_lib.fido.reponse.MakeCredentialResponse;
import com.blue_unicorn.android_auth_lib.fido.request.GetAssertionRequest;
import com.blue_unicorn.android_auth_lib.fido.request.GetInfoRequest;
import com.blue_unicorn.android_auth_lib.fido.request.MakeCredentialRequest;

import io.reactivex.rxjava3.core.Single;

/**
 * Provides the methods of the Authenticator API.
 * Complex methods with user interference are split into two methods in order to authenticate the process in between.
 * See <a href="https://fidoalliance.org/specs/fido-v2.0-id-20180227/fido-client-to-authenticator-protocol-v2.0-id-20180227.html#authenticator-api">specification</a>
 */
public interface AuthenticatorAPI {

    Single<MakeCredentialRequest> makeCredential(MakeCredentialRequest request);

    Single<MakeCredentialResponse> makeInternalCredential(MakeCredentialRequest request);

    Single<GetAssertionRequest> getAssertion(GetAssertionRequest request);

    Single<GetAssertionResponse> getInternalAssertion(GetAssertionRequest request);

    Single<GetInfoResponse> getInfo(GetInfoRequest request);

}
