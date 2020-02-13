package com.blue_unicorn.android_auth_lib.api;

import com.blue_unicorn.android_auth_lib.fido.FidoObject;
import com.blue_unicorn.android_auth_lib.fido.request.RequestObject;
import com.blue_unicorn.android_auth_lib.fido.reponse.ResponseObject;

import io.reactivex.rxjava3.core.Single;

public interface APIHandler {

    Single<FidoObject> callAPI(RequestObject request);

    Single<ResponseObject> updateAPI(RequestObject request);

}
