package com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble;

import com.blue_unicorn.android_auth_lib.android.AuthHandler;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.constants.Command;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.constants.Keepalive;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.constants.Timeout;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;

//TODO: add handling for PROCESSING keepalive command
public class KeepaliveHandler {

    private byte[] rawUPKeepaliveCommand = new byte[]{Command.KEEPALIVE, (byte) 0x00, (byte) 0x01, Keepalive.UP_NEEDED};

    private AuthHandler authHandler;
    private Disposable keepaliveDisposable;

    public KeepaliveHandler(AuthHandler authHandler) {
        this.authHandler = authHandler;
    }

    public void initialize(Observable<byte[]> bluetoothRequests) {
        bluetoothRequests.subscribe(bleReq ->
        {
            if (keepaliveDisposable != null) keepaliveDisposable.dispose();
            keepaliveDisposable = Observable
                .interval(Timeout.kKeepAliveMillis, TimeUnit.MILLISECONDS)
                .map(i -> rawUPKeepaliveCommand)
                    .subscribe(keepaliveCommand -> authHandler.getBleHandler().sendBleData(keepaliveCommand));
        });
    }

    public void stopKeepalive() {
        if (keepaliveDisposable != null) keepaliveDisposable.dispose();
    }
}
