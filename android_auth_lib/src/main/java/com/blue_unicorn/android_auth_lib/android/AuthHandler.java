package com.blue_unicorn.android_auth_lib.android;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.blue_unicorn.android_auth_lib.android.layers.APILayer;
import com.blue_unicorn.android_auth_lib.android.layers.RequestLayer;
import com.blue_unicorn.android_auth_lib.android.layers.ResponseLayer;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.BleHandler;

import io.reactivex.rxjava3.core.Flowable;

public class AuthHandler {

    private RequestLayer requestLayer;
    private APILayer apiLayer;
    private ResponseLayer responseLayer;
    private BleHandler bleHandler;

    AuthHandler(Context context, MutableLiveData<Throwable> fidoAuthServiceErrors) {
        this.requestLayer = new RequestLayer(this);
        this.apiLayer = new APILayer(this, context);
        this.responseLayer = new ResponseLayer(this);
        this.bleHandler = new BleHandler(context, fidoAuthServiceErrors);
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

    public void startAdvertisingProcess() {
        bleHandler.connect();
        requestLayer.initialize(bleHandler.getIncomingBleData());
    }

    public void stopAdvertisingProcess() {
        bleHandler.disconnect();
        bleHandler.removeServices();
    }

    public Flowable<byte[]> getResponses() {
        return responseLayer.getResponses();
    }
}
