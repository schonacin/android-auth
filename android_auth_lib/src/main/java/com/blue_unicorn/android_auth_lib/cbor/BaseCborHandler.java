package com.blue_unicorn.android_auth_lib.cbor;

import androidx.annotation.NonNull;

import com.blue_unicorn.android_auth_lib.fido.reponse.ResponseObject;
import com.blue_unicorn.android_auth_lib.fido.request.RequestObject;

import io.reactivex.rxjava3.core.Single;

public final class BaseCborHandler implements CborHandler {

    @NonNull
    public Single<RequestObject> decode(byte[] input) {
        return CommandBuilder.buildCommand(input)
                .flatMap(CommandMapper::mapRespectiveMethod);
    }

    @NonNull
    public Single<byte[]> encode(ResponseObject input) {
        return ResponseBuilder.buildResponse(input);
    }

}
