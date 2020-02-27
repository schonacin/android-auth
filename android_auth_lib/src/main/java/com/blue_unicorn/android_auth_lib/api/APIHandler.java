package com.blue_unicorn.android_auth_lib.api;

import com.blue_unicorn.android_auth_lib.fido.FidoObject;
import com.blue_unicorn.android_auth_lib.fido.reponse.ResponseObject;
import com.blue_unicorn.android_auth_lib.fido.request.RequestObject;

import io.reactivex.rxjava3.core.Single;

/**
 * Handles the initial and updated calls (i. e. when a user approved a request) to the Authenticator API
 */
public interface APIHandler {

    Single<FidoObject> callAPI(RequestObject request);

    Single<ResponseObject> updateAPI(RequestObject request);

}