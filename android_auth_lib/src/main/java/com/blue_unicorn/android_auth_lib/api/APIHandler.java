package com.blue_unicorn.android_auth_lib.api;

import com.blue_unicorn.android_auth_lib.fido.FidoObject;
import com.blue_unicorn.android_auth_lib.fido.RequestObject;
import com.blue_unicorn.android_auth_lib.fido.ResponseObject;

import io.reactivex.rxjava3.core.Observable;

public interface APIHandler {

    Observable<FidoObject> callAPI(RequestObject request);

    Observable<ResponseObject> updateAPI(RequestObject request);
}
