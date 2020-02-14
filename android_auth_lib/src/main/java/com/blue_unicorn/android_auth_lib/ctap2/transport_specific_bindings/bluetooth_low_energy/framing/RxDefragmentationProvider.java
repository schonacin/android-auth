package com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.bluetooth_low_energy.framing;

import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.bluetooth_low_energy.framing.data.Fragment;
import com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.bluetooth_low_energy.framing.data.Frame;

import io.reactivex.rxjava3.core.Flowable;

public interface RxDefragmentationProvider {

    Flowable<Frame> defragment(Flowable<Fragment> fragments, int maxLen);
}
