package com.blue_unicorn.android_auth_lib.android;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.blue_unicorn.android_auth_lib.android.layers.APILayer;
import com.blue_unicorn.android_auth_lib.android.layers.RequestLayer;
import com.blue_unicorn.android_auth_lib.android.layers.ResponseLayer;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.BleHandler;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.constants.Timeout;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;

public class AuthHandler {

    private RequestLayer requestLayer;
    private APILayer apiLayer;
    private ResponseLayer responseLayer;
    private BleHandler bleHandler;
    private Disposable keepaliveDisposable;

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

    public void stopKeepalive() {
        keepaliveDisposable.dispose();
    }

    public void startAdvertisingProcess() {
        Observable<byte[]> bluetoothRequests = bleHandler.getIncomingBleData().share();

        // TODO: put this in right component
        bluetoothRequests.subscribe(bleReq -> {
            keepaliveDisposable = Observable
                    .interval(Timeout.kKeepAliveMillis, TimeUnit.MILLISECONDS)
                    .map(i -> new byte[]{(byte) 0x82, (byte) 0x00, (byte) 0x01, (byte) 0x02})
                    .subscribe(keepaliveCommand -> bleHandler.sendBleData(keepaliveCommand));
        });

        requestLayer.initialize(bluetoothRequests);
        bleHandler.connect();
    }

    public void stopAdvertisingProcess() {
        bleHandler.disconnect();
        // TODO dispose chains
    }

    public Flowable<byte[]> getResponses() {
        return responseLayer.getResponses();
    }
}
