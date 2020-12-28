package com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.framing;

import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.framing.data.Fragment;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.framing.data.Frame;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;

/*
 * Reactive wrapper for FrameAccumulator.
 */
public interface RxDefragmentationProvider {

    Flowable<Frame> defragment(Flowable<Fragment> fragments, int maxLen);

    Maybe<Frame> defragment(Fragment fragment, int maxLen);
}
