package com.blue_unicorn.android_auth_lib.android.layers;

import com.blue_unicorn.android_auth_lib.android.AuthHandler;
import com.blue_unicorn.android_auth_lib.android.AuthSubscriber;

import org.reactivestreams.Subscriber;

import java.util.LinkedList;
import java.util.List;

public class ResponseLayer {

    private AuthHandler authHandler;

    private List<byte[]> results;
    private Subscriber<byte[]> responseSubscriber;

    public ResponseLayer(AuthHandler authHandler) {
        this.authHandler = authHandler;
        results = new LinkedList<>();
    }

    public Subscriber<byte[]> createNewResponseSubscriber() {
        results.clear();
        this.responseSubscriber = new AuthSubscriber<byte[]>(authHandler) {
            @Override
            public void onNext(byte[] response) {
                // fragments are sent back to bluetooth
                results.add(response);
                request(1);
            }

            @Override
            public void onComplete() {
                authHandler.constructResult(results);
            }
        };
        return responseSubscriber;
    }

    public Subscriber<byte[]> getResponseSubscriber() {
        return responseSubscriber;
    }
}
