package com.blue_unicorn.android_auth_lib.android;

import com.blue_unicorn.android_auth_lib.android.layers.ErrorLayer;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class AuthSubscriber<T> implements Subscriber<T> {

    @Override
    public void onSubscribe(Subscription s) {
    }

    @Override
    public void onComplete() {
    }

    @Override
    public void onError(Throwable e) {
        ErrorLayer.handleErrors(e);
    }

    @Override
    public void onNext(T item) {
    }

}
