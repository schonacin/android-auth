package com.blue_unicorn.android_auth_lib.ctap2.message_encoding;

import androidx.annotation.NonNull;

import com.blue_unicorn.android_auth_lib.ctap2.exceptions.status_codes.InvalidLengthException;
import com.blue_unicorn.android_auth_lib.ctap2.exceptions.status_codes.InvalidParameterException;
import com.blue_unicorn.android_auth_lib.ctap2.message_encoding.data.BaseCommand;
import com.blue_unicorn.android_auth_lib.ctap2.message_encoding.data.Command;
import com.upokecenter.cbor.CBOREncodeOptions;
import com.upokecenter.cbor.CBORObject;

import io.reactivex.rxjava3.core.Single;
import timber.log.Timber;

final class CommandBuilder {

    private static final CBOREncodeOptions ENCODE_OPTIONS = CBOREncodeOptions.DefaultCtap2Canonical;

    private CommandBuilder() {
    }

    @NonNull
    static Single<Command> buildCommand(byte[] input) {
        return Single.defer(() -> {
            if (input.length == 0) {
                return Single.error(new InvalidLengthException());
            }

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
            if (rawParameters.length == 0) {
                return Single.just("");
            }

            CBORObject cborDecodedParameters = CBORObject.DecodeFromBytes(rawParameters, ENCODE_OPTIONS);
            String decodedParameters = cborDecodedParameters.ToJSONString();
            Timber.d("Decoded Parameters from CBOR: %s", decodedParameters);
            return Single.just(decodedParameters);
        }).onErrorResumeNext(throwable -> Single.error(new InvalidParameterException(throwable)));
    }

}
