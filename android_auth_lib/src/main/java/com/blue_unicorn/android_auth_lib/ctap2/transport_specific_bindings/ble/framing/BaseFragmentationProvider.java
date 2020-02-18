package com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.framing;

import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.framing.data.Fragment;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.framing.data.Frame;

import io.reactivex.rxjava3.core.*;

public class BaseFragmentationProvider implements RxFragmentationProvider {

    @Override
    public Flowable<Fragment> fragment(Single<Frame> frame, int maxLen) {
        return frame.flattenAsFlowable(f -> new BaseFrameSplitter(maxLen).split(f));
    }
}
