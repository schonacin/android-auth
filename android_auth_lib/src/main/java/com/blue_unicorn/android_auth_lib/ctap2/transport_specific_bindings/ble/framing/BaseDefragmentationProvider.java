package com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.framing;

import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.framing.data.Fragment;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.framing.data.Frame;

import io.reactivex.rxjava3.core.Flowable;

public class BaseDefragmentationProvider implements RxDefragmentationProvider {

    @Override
    public Flowable<Frame> defragment(Flowable<Fragment> fragments, int maxLen) {

        return fragments.scanWith(() -> new BaseFrameAccumulator(maxLen), (accumulator, fragment) -> {
            if (accumulator.isComplete())
                return new BaseFrameAccumulator(maxLen, fragment);
            accumulator.addFragment(fragment);
            return accumulator;
        }).filter(FrameAccumulator::isComplete).map(FrameAccumulator::getAssembledFrame);
    }
}
