package com.blue_unicorn.android_auth_lib.android;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.blue_unicorn.android_auth_lib.android.constants.IntentExtra;

import io.reactivex.rxjava3.plugins.RxJavaPlugins;

// TODO: set as foreground service?
public class FidoAuthService extends Service {

    private MutableLiveData<Throwable> errors = new MutableLiveData<>();
    private AuthHandler authHandler;

    private final IBinder mBinder = new FidoAuthServiceBinder(this);

    @Override
    public IBinder onBind(Intent intent) {
        RxJavaPlugins.setErrorHandler(e -> {
        });
        // TODO: handle NullPointerException
        Class activityClass = (Class) intent.getExtras().get(IntentExtra.ACTIVITY_CLASS);
        authHandler = new AuthHandler(this, errors, activityClass);
        authHandler.getNotificationHandler().showServiceActiveNotification(this);
        authHandler.startAdvertisingProcess();
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        authHandler.stopAdvertisingProcess();
        authHandler.getNotificationHandler().closeAllNotifications();
        return false;
    }

    public LiveData<Throwable> getErrors() {
        return errors;
    }

    public void handleUserInteraction(boolean approved) {
        authHandler.getApiLayer().buildResponseChainAfterUserInteraction(approved);
    }

    public AuthHandler getAuthHandler() {
        return authHandler;
    }
}
