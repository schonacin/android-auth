package com.blue_unicorn.android_auth_lib.cbor;

import androidx.annotation.NonNull;

import com.blue_unicorn.android_auth_lib.exception.InvalidLengthException;
import com.blue_unicorn.android_auth_lib.exception.InvalidParameterException;
import com.upokecenter.cbor.CBOREncodeOptions;
import com.upokecenter.cbor.CBORObject;

import io.reactivex.rxjava3.core.Single;

final class CommandBuilder {

    private static final CBOREncodeOptions ENCODE_OPTIONS = CBOREncodeOptions.DefaultCtap2Canonical;

    private CommandBuilder() {
    }

    @NonNull
    static Single<Command> buildCommand(byte[] input) {
        return Single.defer(() -> {
            if (input.length == 0)
                return Single.error(new InvalidLengthException());

            byte value = input[0];
            byte[] rawParameters = new byte[input.length - 1];
            System.arraycopy(input, 1, rawParameters, 0, rawParameters.length);

            return decodeParameters(rawParameters)
                    .flatMap(decodedParameters -> Single.just(new BaseCommand(value, decodedParameters)));
        });
    }

    @NonNull
    private static Single<String> decodeParameters(byte[] rawParameters) {
        return Single.defer(() -> {
            if (rawParameters.length == 0)
                return Single.just("");

            CBORObject cborDecodedParameters = CBORObject.DecodeFromBytes(rawParameters, ENCODE_OPTIONS);
            String decodedParameters = cborDecodedParameters.ToJSONString();
            return Single.just(decodedParameters);
        }).onErrorResumeNext(throwable -> Single.error(new InvalidParameterException(throwable)));
    }

}
