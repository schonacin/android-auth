package com.blue_unicorn.android_auth_lib.ctap2.message_encoding;

import androidx.annotation.NonNull;

import com.blue_unicorn.android_auth_lib.ctap2.data.request.RequestObject;
import com.blue_unicorn.android_auth_lib.ctap2.data.response.ResponseObject;

import io.reactivex.rxjava3.core.Single;

/**
 * Handles the Decoding and Encoding of CBOR encoded Byte Arrays
 */
public interface CborHandler {

    @NonNull
    Single<RequestObject> decode(byte[] input);

    @NonNull
    Single<byte[]> encode(ResponseObject input);

}
