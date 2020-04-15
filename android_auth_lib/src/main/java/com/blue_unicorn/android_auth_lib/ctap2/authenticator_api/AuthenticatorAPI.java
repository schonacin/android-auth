package com.blue_unicorn.android_auth_lib.ctap2.authenticator_api;

import com.blue_unicorn.android_auth_lib.ctap2.data.response.GetAssertionResponse;
import com.blue_unicorn.android_auth_lib.ctap2.data.response.GetInfoResponse;
import com.blue_unicorn.android_auth_lib.ctap2.data.response.MakeCredentialResponse;
import com.blue_unicorn.android_auth_lib.ctap2.data.request.GetAssertionRequest;
import com.blue_unicorn.android_auth_lib.ctap2.data.request.GetInfoRequest;
import com.blue_unicorn.android_auth_lib.ctap2.data.request.MakeCredentialRequest;

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
