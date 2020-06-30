package com.blue_unicorn.android_auth_lib.android;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.blue_unicorn.android_auth_lib.android.layers.APILayer;
import com.blue_unicorn.android_auth_lib.android.layers.RequestLayer;
import com.blue_unicorn.android_auth_lib.android.layers.ResponseLayer;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.BleHandler;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.KeepaliveHandler;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Observable;

public class AuthHandler {

    private RequestLayer requestLayer;
    private APILayer apiLayer;
    private ResponseLayer responseLayer;
    private KeepaliveHandler keepaliveHandler;
    private BleHandler bleHandler;

    AuthHandler(Context context, MutableLiveData<Throwable> fidoAuthServiceErrors) {
        this.requestLayer = new RequestLayer(this);
        this.apiLayer = new APILayer(this, context);
        this.responseLayer = new ResponseLayer(this);
        this.keepaliveHandler = new KeepaliveHandler(this);
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

    public KeepaliveHandler getKeepaliveHandler() {
        return keepaliveHandler;
    }

    public BleHandler getBleHandler() {
        return bleHandler;
    }

    public void startAdvertisingProcess() {
        Observable<byte[]> bluetoothRequests = bleHandler.getIncomingBleData().share();
        keepaliveHandler.initialize(bluetoothRequests);
        requestLayer.initialize(bluetoothRequests);
        bleHandler.connect();
    }

    public void stopAdvertisingProcess() {
        keepaliveHandler.stopKeepalive();
        bleHandler.disconnect();
        // TODO dispose chains
    }

    public Flowable<byte[]> getResponses() {
        return responseLayer.getResponses();
    }
}
