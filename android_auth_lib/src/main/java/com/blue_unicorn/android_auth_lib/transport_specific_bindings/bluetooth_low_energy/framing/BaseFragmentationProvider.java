package com.blue_unicorn.android_auth_lib.transport_specific_bindings.bluetooth_low_energy.framing;

import com.blue_unicorn.android_auth_lib.transport_specific_bindings.bluetooth_low_energy.framing.data.Fragment;
import com.blue_unicorn.android_auth_lib.transport_specific_bindings.bluetooth_low_energy.framing.data.Frame;

import io.reactivex.rxjava3.core.*;

public class BaseFragmentationProvider implements RxFragmentationProvider {

    @Override
    public Flowable<Fragment> fragment(Single<Frame> frame, int maxLen) {
        return frame.flattenAsFlowable(f -> new BaseFrameSplitter(maxLen).split(f));
    }
}
