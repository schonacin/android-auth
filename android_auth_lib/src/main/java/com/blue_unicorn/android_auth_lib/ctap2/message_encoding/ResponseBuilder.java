package com.blue_unicorn.android_auth_lib.ctap2.message_encoding;

import androidx.annotation.NonNull;

import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.response.GetAssertionResponse;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.response.GetInfoResponse;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.response.MakeCredentialResponse;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.response.ResponseObject;
import com.blue_unicorn.android_auth_lib.ctap2.constants.StatusCode;
import com.blue_unicorn.android_auth_lib.ctap2.exceptions.AuthLibException;
import com.blue_unicorn.android_auth_lib.util.ArrayUtil;

import io.reactivex.rxjava3.core.Single;

final class ResponseBuilder {

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
                return Single.error(new AuthLibException("Unknown Response Object!"));
            }
        });
    }

    @NonNull
    private static Single<byte[]> prependSuccessStatus(byte[] encodedData) {
        return Single.fromCallable(() -> ArrayUtil.concatBytes(new byte[]{StatusCode.CTAP2_OK}, encodedData));
    }
}
