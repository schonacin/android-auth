package com.blue_unicorn.android_auth_lib.cbor;

import com.blue_unicorn.android_auth_lib.exception.InvalidLengthException;
import com.blue_unicorn.android_auth_lib.exception.InvalidParameterException;
import com.upokecenter.cbor.CBOREncodeOptions;
import com.upokecenter.cbor.CBORObject;

import io.reactivex.rxjava3.core.Single;

abstract class CommandBuilder {

    private static final CBOREncodeOptions ENCODE_OPTIONS = CBOREncodeOptions.DefaultCtap2Canonical;

    static Single<Command> buildCommand(byte[] input) {

        return Single.defer(() -> {
            if (input.length == 0) {
                return Single.error(new InvalidLengthException());
            }
            byte value = input[0];
            byte[] rawParameters = new byte[input.length -1];
            System.arraycopy(input, 1, rawParameters, 0, rawParameters.length);

            return decodeParameters(rawParameters)
                    .flatMap(decodedParameters -> Single.just(new BaseCommand(value, decodedParameters)));
        });

    }

    private static Single<String> decodeParameters(byte[] rawParameters) {

        return Single.defer(() -> {

            if (rawParameters.length == 0)
                return Single.just("");

            try {
                CBORObject cborDecodedParameters = CBORObject.DecodeFromBytes(rawParameters, ENCODE_OPTIONS);
                return Single.just(cborDecodedParameters.ToJSONString());

            } catch(Exception e) {
                // if any error encounters during the encoding process we assume that the request is invalid
                return Single.error(new InvalidParameterException());
            }

        });

    }

}
