package com.blue_unicorn.android_auth_lib.android;

import com.blue_unicorn.android_auth_lib.android.layers.ErrorLayer;

import io.reactivex.rxjava3.subscribers.DefaultSubscriber;


public abstract class AuthSubscriber<T> extends DefaultSubscriber<T> {

    private AuthHandler authHandler;

    protected AuthSubscriber(AuthHandler authHandler) {
        this.authHandler = authHandler;
    }

    @Override
    public void onStart() {
        request(1);
    }

    @Override
    public void onComplete() {
    }

    @Override
    public void onError(Throwable e) {
        ErrorLayer.handleErrors(authHandler, e);
    }

}
