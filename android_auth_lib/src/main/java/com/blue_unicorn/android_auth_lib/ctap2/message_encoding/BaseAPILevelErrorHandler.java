package com.blue_unicorn.android_auth_lib.ctap2.message_encoding;

import androidx.annotation.NonNull;

import com.blue_unicorn.android_auth_lib.ctap2.exceptions.StatusCodeException;

import io.reactivex.rxjava3.core.Single;

public class BaseAPILevelErrorHandler implements APILevelErrorHandler {

    @NonNull
    public Single<byte[]> convertErrors(byte[] input) {
        return Single.fromCallable(() -> input)
                .onErrorResumeNext(throwable -> {
                    if (throwable instanceof StatusCodeException) {
                        return Single.just(new byte[]{});
                    } else {
                        return Single.error(throwable);
                    }
                });
    }

}
