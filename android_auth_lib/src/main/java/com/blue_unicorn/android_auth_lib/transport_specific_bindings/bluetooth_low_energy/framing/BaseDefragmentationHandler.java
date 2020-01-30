package com.blue_unicorn.android_auth_lib.transport_specific_bindings.bluetooth_low_energy.framing;

import com.blue_unicorn.android_auth_lib.transport_specific_bindings.bluetooth_low_energy.framing.data.Fragment;
import com.blue_unicorn.android_auth_lib.transport_specific_bindings.bluetooth_low_energy.framing.data.Frame;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

// assumption:
// maxLen does not change in between transmission of fragments of the same frame
public class BaseDefragmentationHandler implements RxDefragmentationHandler {

    // accepts Flowable that emits all Fragments belonging to the same Frame
    // constructs Frame from Fragments while receiving them and emits it when completed
    public Single<Frame> defragment(Flowable<Fragment> fragments, int maxLen) {
        return fragments.collect(() -> new BaseFrameBuffer(maxLen), FrameBuffer::addFragment)
                .map(FrameBuffer::getFrame);
    }
}
