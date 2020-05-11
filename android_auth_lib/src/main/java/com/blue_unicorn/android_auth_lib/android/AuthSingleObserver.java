package com.blue_unicorn.android_auth_lib.android;

import com.blue_unicorn.android_auth_lib.android.layers.ErrorLayer;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.FidoObject;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.request.RequestObject;
import com.blue_unicorn.android_auth_lib.ctap2.authenticator_api.data.response.ResponseObject;

import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;

public class AuthSingleObserver<T> implements SingleObserver<T> {
    @Override
    public void onSubscribe(Disposable d){
    }

    @Override
    public void onSuccess(T item){
    }

    @Override
    public void onError(Throwable t) {
        ErrorLayer.handleErrors(t);
    }
}
