package com.blue_unicorn.android_auth_lib.ctap2.message_encoding;

import androidx.annotation.NonNull;

import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.request.RequestObject;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.response.ResponseObject;

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
