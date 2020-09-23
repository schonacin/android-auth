package com.blue_unicorn.android_auth_lib.android.layers;

import com.blue_unicorn.android_auth_lib.android.AuthHandler;
import com.blue_unicorn.android_auth_lib.ctap2.exceptions.StatusCodeException;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.exceptions.BleException;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.framing.BaseFragmentationProvider;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.framing.RxFragmentationProvider;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.framing.data.BaseFrame;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.framing.data.Fragment;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.framing.data.Frame;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import timber.log.Timber;

public final class ErrorLayer {

    public static void handleErrors(AuthHandler authHandler, Throwable throwable) {

        RxFragmentationProvider fragmentationProvider = new BaseFragmentationProvider();

        if (throwable instanceof BleException) {
            Flowable.fromCallable(((BleException) throwable)::getErrorCode)
                    .map(b -> new byte[]{b})
                    .subscribe(authHandler.getResponseLayer().getResponseSubscriber());
        } else if (throwable instanceof StatusCodeException) {
            Single.fromCallable(((StatusCodeException) throwable)::getErrorCode)
                    .map(e -> new byte[]{(byte) (int) e})
                    .map(BaseFrame::new)
                    .cast(Frame.class)
                    .flatMapPublisher(frame -> fragmentationProvider.fragment(Single.just(frame), authHandler.getBleHandler().getMtu()))
                    .map(Fragment::asBytes)
                    .subscribe(authHandler.getResponseLayer().getResponseSubscriber());
        } else {
            Timber.d(throwable, "Something went wrong :(");
        }

    }

}
