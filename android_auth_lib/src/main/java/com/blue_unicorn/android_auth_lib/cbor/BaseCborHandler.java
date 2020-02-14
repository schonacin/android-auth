package com.blue_unicorn.android_auth_lib.cbor;

import androidx.annotation.NonNull;

import com.blue_unicorn.android_auth_lib.fido.RequestObject;

import io.reactivex.rxjava3.core.Single;

public final class BaseCborHandler implements CborHandler {

    @NonNull
    public Single<RequestObject> decode(byte[] input) {
        return CommandBuilder.buildCommand(input)
                .flatMap(CommandMapper::mapRespectiveMethod);
    }

}