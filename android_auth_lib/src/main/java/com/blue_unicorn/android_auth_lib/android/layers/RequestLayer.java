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
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subscribers.DisposableSubscriber;

public class RequestLayer {

    private RxDefragmentationProvider defragmentationProvider;

    private DisposableSubscriber<byte[]> frameSubscriber;

    private AuthHandler authHandler;

    private PublishSubject<byte[]> incomingRequests;

    public RequestLayer(AuthHandler authHandler) {
        this.authHandler = authHandler;
        this.defragmentationProvider = new BaseDefragmentationProvider();
    }

    public void initialize(Observable<byte[]> bluetoothRequests) {
        // call this method when Bluetooth is activated and advertising starts.
        // this basically starts the observable chain
        incomingRequests = PublishSubject.create();
        buildNewFragmentChain();
        bluetoothRequests
                .subscribe(incomingRequests);
    }

    private void resetChain() {
        incomingRequests = PublishSubject.create();
        buildNewFragmentChain();
        authHandler.getBleHandler().getIncomingBleData()
                .subscribe(incomingRequests);
    }

    private void buildNewFragmentChain() {
        frameSubscriber = new AuthSubscriber<byte[]>(authHandler) {
            @Override
            public void onNext(byte[] frame) {
                authHandler.getApiLayer().buildNewRequestChain(frame);
                request(1);
            }

            @Override
            public void onError(Throwable t) {
                super.onError(t);
                dispose();
                if(incomingRequests.hasThrowable()) {
                    resetChain();
                } else {
                    buildNewFragmentChain();
                }
            }
        };

        incomingRequests
                .toFlowable(BackpressureStrategy.BUFFER)
                .flatMapSingle(this::toFragment)
                .flatMapMaybe(fragment -> defragmentationProvider.defragment(fragment, authHandler.getBleHandler().getMtu()))
                .map(Frame::getDATA)
                .subscribe(frameSubscriber);
    }

    // TODO: put this in right component
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
}
