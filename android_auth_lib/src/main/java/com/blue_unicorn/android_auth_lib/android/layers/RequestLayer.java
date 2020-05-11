package com.blue_unicorn.android_auth_lib.android.layers;

import com.blue_unicorn.android_auth_lib.android.AuthHandler;
import com.blue_unicorn.android_auth_lib.android.AuthSubscriber;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.framing.BaseDefragmentationProvider;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.framing.RxDefragmentationProvider;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.framing.data.BaseContinuationFragment;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.framing.data.BaseInitializationFragment;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.framing.data.Fragment;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.framing.data.Frame;

import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.subscribers.DefaultSubscriber;

public class RequestLayer {

    private RxDefragmentationProvider defragmentationProvider;

    private DefaultSubscriber<byte[]> frameSubscriber;

    private AuthHandler authHandler;

    public RequestLayer(AuthHandler authHandler) {
        this.authHandler = authHandler;
        this.defragmentationProvider = new BaseDefragmentationProvider();
    }

    private Observable<byte[]> getBluetoothRequests() {
        //emulating incoming Bluetooth Requests
        return Observable.just(new byte[]{(byte) 0x83, 0x02, 0x03, 0x04});
    }

    public void initialize(Observable<byte[]> bluetoothRequest) {
        // call this method when Bluetooth is activated and advertising starts.
        // this basically starts the observable chain

        frameSubscriber = new AuthSubscriber<byte[]>(authHandler) {
            @Override
            public void onNext(byte[] frame) {
                authHandler.getApiLayer().buildNewRequestChain(frame);
                request(1);
            }
        };

        bluetoothRequest
                .flatMapSingle(this::toFragment)
                .flatMapMaybe(fragment -> defragmentationProvider.defragment(fragment, getMaxLength()))
                .map(Frame::getDATA)
                .toFlowable(BackpressureStrategy.BUFFER)
                .subscribe(frameSubscriber);
    }

    private Single<Fragment> toFragment(byte[] request) {
        return Single.just(request)
                .map(bytes -> {
                    if ((bytes[0] & (byte) 0x80) == (byte) 0x80) {
                        return new BaseInitializationFragment(bytes);
                    } else {
                        return new BaseContinuationFragment(bytes);
                    }
                })
                .cast(Fragment.class);
    }

    private int getMaxLength() {
        return 20;
    }
}
