package com.blue_unicorn.android_auth_lib.android.layers;

import io.reactivex.rxjava3.core.Observable;

public interface RequestLayer {

    void initialize(Observable<byte[]> bluetoothRequests);

    void disposeChain();

}
