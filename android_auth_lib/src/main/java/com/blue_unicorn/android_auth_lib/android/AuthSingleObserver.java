package com.blue_unicorn.android_auth_lib.android;

import com.blue_unicorn.android_auth_lib.android.layers.ErrorLayer;

import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;

public class AuthSingleObserver<T> implements SingleObserver<T> {

    private AuthHandler authHandler;

    protected AuthSingleObserver(AuthHandler authHandler) {
        this.authHandler = authHandler;
    }

    @Override
    public void onSubscribe(Disposable d) {
    }

    @Override
    public void onSuccess(T item) {
    }

    @Override
    public void onError(Throwable t) {
        ErrorLayer.handleErrors(authHandler, t);
    }

}
