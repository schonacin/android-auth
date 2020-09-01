package com.blue_unicorn.android_auth_lib.android.layers;

import io.reactivex.rxjava3.core.Observable;

/**
 * Handles the incoming byte requests and accumulates them to valid requests
 */
public interface RequestLayer {

    void initialize(Observable<byte[]> bluetoothRequests);

    void disposeChain();

}
