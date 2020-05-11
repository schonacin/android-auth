package com.blue_unicorn.android_auth_lib.android.layers;

import com.blue_unicorn.android_auth_lib.ctap2.exceptions.StatusCodeException;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.exceptions.BleException;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.framing.BaseFragmentationProvider;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.framing.RxFragmentationProvider;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.framing.data.BaseFrame;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.framing.data.Fragment;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.framing.data.Frame;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

public final class ErrorLayer {

    public static void handleErrors(Throwable t) {

        ResponseLayer responseLayer = new ResponseLayer();
        RxFragmentationProvider fragmentationProvider = new BaseFragmentationProvider();

        if (t instanceof BleException) {
            Flowable.fromCallable(((BleException) t)::getErrorCode)
                    .map(b -> new byte[]{b})
                    .subscribe(responseLayer.createNewResponseSubscriber());
        } else if (t instanceof StatusCodeException) {
            Single.just((byte) 0x30)
                    .map(b -> new byte[]{b})
                    .map(BaseFrame::new)
                    .cast(Frame.class)
                    .flatMapPublisher(frame -> fragmentationProvider.fragment(Single.just(frame), getMaxLength()))
                    .map(Fragment::asBytes)
                    .subscribe(responseLayer.createNewResponseSubscriber());
        } else {
            // handle this shit
        }

    }

    private static int getMaxLength() {
        return 20;
    }

}
