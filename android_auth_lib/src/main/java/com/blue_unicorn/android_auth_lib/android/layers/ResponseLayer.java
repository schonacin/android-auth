package com.blue_unicorn.android_auth_lib.android.layers;

import com.blue_unicorn.android_auth_lib.android.AuthSubscriber;

import org.reactivestreams.Subscriber;

public class ResponseLayer {

    private Subscriber<byte[]> responseSubscriber;

    public Subscriber<byte[]> createNewResponseSubscriber() {
        this.responseSubscriber = new AuthSubscriber<byte[]>() {
            @Override
            public void onNext(byte[] response) {
                // fragments are sent back to bluetooth
                request(1);
            }
        };
        return responseSubscriber;
    }

}
