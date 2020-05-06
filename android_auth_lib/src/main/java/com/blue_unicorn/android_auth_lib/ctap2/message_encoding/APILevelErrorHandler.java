package com.blue_unicorn.android_auth_lib.ctap2.message_encoding;

import io.reactivex.rxjava3.core.Single;

public interface APILevelErrorHandler {

    Single<byte[]> convertErrors(byte[] input);

}
