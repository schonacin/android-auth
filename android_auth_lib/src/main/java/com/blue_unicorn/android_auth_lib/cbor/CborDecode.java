package com.blue_unicorn.android_auth_lib.cbor;

import com.blue_unicorn.android_auth_lib.fido.FidoObject;

import io.reactivex.rxjava3.core.Single;

public interface CborDecode {

    Single<FidoObject> decode(byte[] input);

}
