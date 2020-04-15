package com.blue_unicorn.android_auth_lib.ctap2.message_encoding;

import androidx.annotation.NonNull;

import com.blue_unicorn.android_auth_lib.ctap2.data.response.GetAssertionResponse;
import com.blue_unicorn.android_auth_lib.ctap2.data.response.GetInfoResponse;
import com.blue_unicorn.android_auth_lib.ctap2.data.response.MakeCredentialResponse;
import com.blue_unicorn.android_auth_lib.ctap2.data.response.ResponseObject;
import com.blue_unicorn.android_auth_lib.ctap2.message_encoding.exceptions.AndroidAuthLibException;

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

    @NonNull
    private static Single<byte[]> mapRespectiveMethodToCBOR(ResponseObject responseObject) {
        return Single.defer(() -> {
            if (responseObject instanceof GetInfoResponse ||
                    responseObject instanceof MakeCredentialResponse ||
                    responseObject instanceof GetAssertionResponse) {
                return CborSerializer.serialize(responseObject);
            } else {
                return Single.error(new AndroidAuthLibException("Unknown Response Object!"));
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
