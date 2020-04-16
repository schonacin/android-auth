package com.blue_unicorn.android_auth_lib.ctap2.authenticator_api;

import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.FidoObject;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.request.RequestObject;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.response.ResponseObject;

import io.reactivex.rxjava3.core.Single;

/**
 * Handles the initial and updated calls (i. e. when a user approved a request) to the Authenticator API
 */
public interface APIHandler {

    Single<FidoObject> callAPI(RequestObject request);

    Single<ResponseObject> updateAPI(RequestObject request);

}
