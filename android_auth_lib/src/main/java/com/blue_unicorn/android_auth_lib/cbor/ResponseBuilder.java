package com.blue_unicorn.android_auth_lib.cbor;

import androidx.annotation.NonNull;

import com.blue_unicorn.android_auth_lib.exception.AndroidAuthLibException;
import com.blue_unicorn.android_auth_lib.fido.reponse.GetAssertionResponse;
import com.blue_unicorn.android_auth_lib.fido.reponse.GetInfoResponse;
import com.blue_unicorn.android_auth_lib.fido.reponse.MakeCredentialResponse;
import com.blue_unicorn.android_auth_lib.fido.reponse.ResponseObject;

import io.reactivex.rxjava3.core.Single;

final class ResponseBuilder {

    private static final byte[] CTAP1_ERR_SUCCESS = new byte[]{0x00};

    private ResponseBuilder() {
    }

    @NonNull
    static Single<byte[]> buildResponse(ResponseObject responseObject) {
        return mapRespectiveMethodToCBOR(responseObject)
                .flatMap(ResponseBuilder::prependSuccessStatus);
    }

    private static Single<byte[]> mapRespectiveMethodToCBOR(ResponseObject responseObject) {
        return Single.defer(() -> {
            if(responseObject instanceof GetInfoResponse ||
                    responseObject instanceof MakeCredentialResponse ||
                    responseObject instanceof GetAssertionResponse) {
                return Single.fromCallable(() -> CborSerializer.serialize(responseObject));
            } else {
                return Single.error(new AndroidAuthLibException());
            }
        });
    }

    @NonNull
    private static Single<byte[]> prependSuccessStatus(byte[] encodedData) {
        return Single.fromCallable(() -> {
            byte[] completeResponse = new byte[CTAP1_ERR_SUCCESS.length + encodedData.length];
            System.arraycopy(CTAP1_ERR_SUCCESS, 0, completeResponse, 0, CTAP1_ERR_SUCCESS.length);
            System.arraycopy(encodedData, 0, completeResponse, CTAP1_ERR_SUCCESS.length, encodedData.length);

            return completeResponse;
        });
    }
}
