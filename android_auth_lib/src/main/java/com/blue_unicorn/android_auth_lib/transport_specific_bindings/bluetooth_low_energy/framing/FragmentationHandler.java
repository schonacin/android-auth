package com.blue_unicorn.android_auth_lib.transport_specific_bindings.bluetooth_low_energy.framing;

import io.reactivex.rxjava3.core.*;

// assumption:
// maxLen does not change in between transmission of fragments of the same frame
public final class FragmentationHandler {

    // accepts Flowable that emits Fragments belonging to the same Frame
    public static Single<BaseFrame> defragment(Flowable<BaseFragment> fragments, int maxLen) {
        return fragments.collect(() -> new BaseFrameBuffer(maxLen), BaseFrameBuffer::addFragment)
                        .map(BaseFrameBuffer::getFrame);
    }

    public static Flowable<BaseFragment> fragment(Single<BaseFrame> frame, int maxLen) {
        return frame.flattenAsFlowable(f -> new BaseFrameBuffer(maxLen, f).getFragments());
    }
}
