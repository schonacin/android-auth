package com.blue_unicorn.android_auth_lib.cbor;

import com.blue_unicorn.android_auth_lib.exception.AndroidAuthLibException;
import com.blue_unicorn.android_auth_lib.fido.GetAssertionResponse;
import com.blue_unicorn.android_auth_lib.fido.GetInfoResponse;
import com.blue_unicorn.android_auth_lib.fido.MakeCredentialResponse;
import com.blue_unicorn.android_auth_lib.fido.ResponseObject;
import com.google.gson.Gson;

import io.reactivex.rxjava3.core.Single;

final class ResponseMapper {

    private ResponseMapper() {
    }

    static Single<String> mapRespectiveMethod(ResponseObject responseObject) {
        return Single.defer(() -> {
            if(responseObject instanceof GetInfoResponse ||
                    responseObject instanceof MakeCredentialResponse ||
                    responseObject instanceof GetAssertionResponse) {
                return processResponse(responseObject);
            } else {
                return Single.error(new AndroidAuthLibException());
            }
        });
    }

    private static Single<String> processResponse(ResponseObject response) {
        return Single.fromCallable(() -> {
            Gson gson = new Gson();
            return gson.toJson(response);
        });
    }

}
