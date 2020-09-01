package com.blue_unicorn.android_auth_lib.android;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.blue_unicorn.android_auth_lib.android.layers.APILayer;
import com.blue_unicorn.android_auth_lib.android.layers.BaseAPILayer;
import com.blue_unicorn.android_auth_lib.android.layers.BaseRequestLayer;
import com.blue_unicorn.android_auth_lib.android.layers.BaseResponseLayer;
import com.blue_unicorn.android_auth_lib.android.layers.RequestLayer;
import com.blue_unicorn.android_auth_lib.android.layers.ResponseLayer;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.BleHandler;

import io.reactivex.rxjava3.core.Flowable;

public class BaseAuthHandler implements AuthHandler {

    private RequestLayer requestLayer;
    private APILayer apiLayer;
    private ResponseLayer responseLayer;
    private BleHandler bleHandler;
    private NotificationHandler notificationHandler;
    private Class activityClass;

    public BaseAuthHandler(Context context, MutableLiveData<Throwable> fidoAuthServiceErrors, Class activityClass) {
        this.requestLayer = new BaseRequestLayer(this);
        this.apiLayer = new BaseAPILayer(this, context);
        this.responseLayer = new BaseResponseLayer(this);
        this.bleHandler = new BleHandler(context, fidoAuthServiceErrors);
        this.notificationHandler = new BaseNotificationHandler(context, activityClass);
        this.activityClass = activityClass;
    }

    public Class getActivityClass() {
        return activityClass;
    }

    public RequestLayer getRequestLayer() {
        return requestLayer;
    }

    public APILayer getApiLayer() {
        return apiLayer;
    }

    public ResponseLayer getResponseLayer() {
        return responseLayer;
    }

    public BleHandler getBleHandler() {
        return bleHandler;
    }

    public NotificationHandler getNotificationHandler() {
        return notificationHandler;
    }

    public void startAdvertisingProcess() {
        bleHandler.connect();
        requestLayer.initialize(bleHandler.getIncomingBleData());
    }

    public void stopAdvertisingProcess() {
        bleHandler.disconnect();
        bleHandler.removeServices();
        requestLayer.disposeChain();
        responseLayer.disposeChain();
    }

    public Flowable<byte[]> getResponses() {
        return responseLayer.getResponses();
    }
}
