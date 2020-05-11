package com.blue_unicorn.android_auth_lib.android.layers;

import android.content.Context;

import com.blue_unicorn.android_auth_lib.android.AuthSubscriber;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.framing.BaseDefragmentationProvider;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.framing.RxDefragmentationProvider;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.framing.data.BaseContinuationFragment;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.framing.data.BaseInitializationFragment;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.framing.data.Fragment;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.framing.data.Frame;

import org.reactivestreams.Subscriber;

import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Observable;

public class RequestLayer {

    private RxDefragmentationProvider defragmentationProvider;

    private Subscriber<byte[]> frameSubscriber;

    private APILayer apiLayer;

    RequestLayer(Context context) {
        this.defragmentationProvider = new BaseDefragmentationProvider();
        this.apiLayer = new APILayer(context);
    }

    private Observable<byte[]> getBluetoothRequests() {
        //emulating incoming Bluetooth Requests
        return Observable.just(new byte[]{(byte)0x83,0x02,0x03,0x04});
    }

    void initialize(Observable<byte[]> bluetoothRequest) {
        // call this method when Bluetooth is activated and advertising starts.
        // this basically starts the observable chain

        frameSubscriber = new AuthSubscriber<byte[]>() {
            @Override
            public void onComplete() {
            }

            @Override
            public void onNext(byte[] frame) {
                apiLayer.buildNewRequestChain(frame);
            }
        };

        defragmentationProvider.defragment(getFragments(bluetoothRequest), getMaxLength())
                .map(Frame::getDATA)
                .subscribe(frameSubscriber);
    }

    private Flowable<Fragment> getFragments(Observable<byte[]> incomingBluetoothRequests) {
        return incomingBluetoothRequests
                .map(bytes -> {
                    if((bytes[0] & (byte)0x80) == (byte)0x80) {
                        return new BaseInitializationFragment(bytes);
                    } else {
                        return new BaseContinuationFragment(bytes);
                    }
                })
                .cast(Fragment.class)
                .toFlowable(BackpressureStrategy.BUFFER);
    }

    private int getMaxLength() {
        return 20;
    }

}
