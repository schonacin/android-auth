package com.blue_unicorn.android_auth_lib.cbor;

import com.blue_unicorn.android_auth_lib.fido.RequestObject;

import io.reactivex.rxjava3.core.Single;

/**
 * Handles the Decoding (and later ENCODING)of CBOR encoded Byte Arrays
 *
 */
public interface CborHandler {

    Single<RequestObject> decode(byte[] input);

}
