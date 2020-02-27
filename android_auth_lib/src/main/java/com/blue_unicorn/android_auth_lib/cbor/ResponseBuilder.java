package com.blue_unicorn.android_auth_lib.cbor;

import androidx.annotation.NonNull;

import com.blue_unicorn.android_auth_lib.exception.AndroidAuthLibException;
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
        return encodeData(data)
                .flatMap(ResponseBuilder::prependSuccessStatus);
    }

    @NonNull
    private static Single<byte[]> encodeData(String data) {
        return Single.fromCallable(() -> CBORObject.FromJSONString(data, JSON_OPTIONS))
                .flatMap(ResponseBuilder::adjustCborObject)
                .map(transformedObject -> transformedObject.EncodeToBytes(ENCODE_OPTIONS))
                .onErrorResumeNext(throwable -> Single.error(new AndroidAuthLibException(throwable)));
    }

    @NonNull
    private static Single<CBORObject> adjustCborObject(CBORObject cborDecodedResponse) {
        return Single.fromCallable(() -> transformCBORMap(cborDecodedResponse, true));
    }

    // very hacky, might not be final
    // this method covers two functions:
    //  transform the first layer of keys into integers
    //  transform array of bytes into bytestring
    @NonNull
    private static CBORObject transformCBORMap(CBORObject map, boolean isFirstLayer) {
        Collection<CBORObject> cborMapKeys = map.getKeys();
        List<String> keys = new LinkedList<>();
        for( CBORObject key: cborMapKeys) {
            keys.add(key.AsString());
        }

        CBORObject transformedCBORObject = CBORObject.NewMap();
        for( String key: keys) {
            CBORObject innerObject = map.get(key);

            //The elements inside the Array that must be transformed are of Type Integer
            if(innerObject.getType() == CBORType.Array && innerObject.size() > 0 && innerObject.get(0).getType() == CBORType.Integer) {
                // the indivdual Byte Objects are extracted and put together into an array which CBOR will read as a ByteString
                int length = innerObject.size();
                byte[] byteArray = new byte[length];

                for( int i = 0; i < length; i++) {
                    CBORObject cborByte = innerObject.get(i);
                    byte transformedByte = cborByte.AsNumber().ToByteUnchecked();
                    byteArray[i] = transformedByte;
                }

                innerObject = CBORObject.FromObject(byteArray);
            } else if(innerObject.getType() == CBORType.Map) {
                // if the innerObject is a map, we call this function recursively as this map could also include bytes
                innerObject = transformCBORMap(innerObject, false);
            } else if(innerObject.getType() == CBORType.Array) {
                // if the innerObject is an array of maps, we call the function recursively for each map
                for(int i=0; i<innerObject.size(); i++) {
                    if(innerObject.get(i).getType() == CBORType.Map) {
                        innerObject.set(i, transformCBORMap(innerObject.get(i), false));
                    }
                }
            }
            // for any other primitive types, nothing has to be transformed

            // in the first layer, the keys must be ints (i. e. 1 instead of "1")
            if(isFirstLayer) {
                int keyAsInt = Integer.parseInt(key);
                transformedCBORObject.set(keyAsInt, innerObject);
            } else {
                transformedCBORObject.set(key, innerObject);
            }
        }

        return transformedCBORObject;
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
