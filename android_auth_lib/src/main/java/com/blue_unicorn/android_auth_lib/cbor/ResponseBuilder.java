package com.blue_unicorn.android_auth_lib.cbor;

import androidx.annotation.NonNull;

import com.upokecenter.cbor.CBOREncodeOptions;
import com.upokecenter.cbor.CBORObject;
import com.upokecenter.cbor.CBORType;
import com.upokecenter.cbor.JSONOptions;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import io.reactivex.rxjava3.core.Single;

final class ResponseBuilder {

    private static final CBOREncodeOptions ENCODE_OPTIONS = CBOREncodeOptions.DefaultCtap2Canonical;
    private static final JSONOptions JSON_OPTIONS = new JSONOptions("base64padding=true;replacesurrogates=false;numberconversion=IntOrFloat;allowduplicatekeys=false");

    private static final byte[] CTAP1_ERR_SUCCESS = new byte[]{0x00};

    private ResponseBuilder() {
    }

    @NonNull
    static Single<byte[]> buildResponse(String data) {
        return Single.defer(() -> encodeData(data)
                .flatMap(ResponseBuilder::prependSuccessStatus));
    }

    @NonNull
    private static Single<byte[]> encodeData(String data) {
        return Single.defer(() -> {
            CBORObject cborDecodedResponse = CBORObject.FromJSONString(data, JSON_OPTIONS);

            return adjustCborObject(cborDecodedResponse)
                    .map(transformedObject -> transformedObject.EncodeToBytes(ENCODE_OPTIONS))
                    .flatMap(Single::just);

        });
    }

    @NonNull
    private static Single<CBORObject> adjustCborObject(CBORObject cborDecodedResponse) {
        return Single.defer(() -> {
            Collection<CBORObject> cborObjectKeys = cborDecodedResponse.getKeys();
            List<String> keys = new LinkedList<>();
            for( CBORObject key: cborObjectKeys) {
                keys.add(key.AsString());
            }

            CBORObject transformedCBORObject = CBORObject.NewMap();
            for(String key: keys) {
                CBORObject innerObject = cborDecodedResponse.get(key);
                int integerKey = Integer.parseInt(key);
                CBORType[] types = CBORType.values();
                if(innerObject.getType() == CBORType.Array && innerObject.size() > 0 && innerObject.get(0).getType() == CBORType.Integer) {
                    int length = innerObject.size();
                    byte[] byteArray = new byte[innerObject.size()];
                    for( int i = 0; i < innerObject.size(); i++) {
                        CBORObject cborByte = innerObject.get(i);
                        byte transformedByte = cborByte.AsNumber().ToByteUnchecked();
                        byteArray[i] = transformedByte;
                    }
                    innerObject = CBORObject.FromObject(byteArray);
                }
                transformedCBORObject.set(integerKey, innerObject);
            }

            return Single.just(transformedCBORObject);
        });
    }

    @NonNull
    private static Single<byte[]> prependSuccessStatus(byte[] encodedData) {
        return Single.defer(() -> {

            byte[] completeResponse = new byte[CTAP1_ERR_SUCCESS.length + encodedData.length];
            System.arraycopy(CTAP1_ERR_SUCCESS, 0, completeResponse, 0, CTAP1_ERR_SUCCESS.length);
            System.arraycopy(encodedData, 0, completeResponse, CTAP1_ERR_SUCCESS.length, encodedData.length);

            return Single.just(completeResponse);

        });
    }
}
