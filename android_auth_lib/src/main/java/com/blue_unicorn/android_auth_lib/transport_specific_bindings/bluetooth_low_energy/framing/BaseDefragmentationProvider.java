package com.blue_unicorn.android_auth_lib.transport_specific_bindings.bluetooth_low_energy.framing;

import com.blue_unicorn.android_auth_lib.transport_specific_bindings.bluetooth_low_energy.framing.data.Fragment;
import com.blue_unicorn.android_auth_lib.transport_specific_bindings.bluetooth_low_energy.framing.data.Frame;

import io.reactivex.rxjava3.core.Flowable;

public abstract class BaseDefragmentationProvider implements RxDefragmentationProvider {

    // accepts Flowable that emits all incoming Fragments, for a fixed maxLen
    // constructs Frames from Fragments while receiving them and emits each Frame when completed
    // TODO: could be maybe done more elegantly with windowUntil + collect?
    @Override
    public Flowable<Frame> defragment(Flowable<Fragment> fragments, int maxLen) {
        return fragments.scanWith(() -> new BaseFrameAccumulator(maxLen), (accumulator, fragment) -> {
            if(accumulator.isComplete())
                return new BaseFrameAccumulator(maxLen, fragment);
            accumulator.addFragment(fragment);
            return accumulator;
        }).filter(FrameAccumulator::isComplete).map(FrameAccumulator::getAssembledFrame);
    }
}
