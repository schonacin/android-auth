package com.blue_unicorn.android_auth_lib.android;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.BleHandler;

import io.reactivex.rxjava3.plugins.RxJavaPlugins;

// TODO: set as foreground service?
public class FidoAuthService extends Service {

    private MutableLiveData<Throwable> errors = new MutableLiveData<>();
    private BleHandler bleHandler;
    private AuthHandler authHandler;
    private Class activityClass;

    private final IBinder mBinder = new FidoAuthServiceBinder(this);

    @Override
    public IBinder onBind(Intent intent) {
        RxJavaPlugins.setErrorHandler(e -> {
        });
        authHandler = new AuthHandler(this, errors);
        authHandler.startAdvertisingProcess();

        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        authHandler.stopAdvertisingProcess();
        return false;
    }

    public LiveData<Throwable> getErrors() {
        return errors;
    }

    public void setActivityClass(Class activityClass) {
        this.activityClass = activityClass;
        authHandler.setActivityClass(activityClass);
    }

    public void handleUserInteraction(boolean approved) {
        authHandler.getApiLayer().buildResponseChainAfterUserInteraction(approved);
    }

}
