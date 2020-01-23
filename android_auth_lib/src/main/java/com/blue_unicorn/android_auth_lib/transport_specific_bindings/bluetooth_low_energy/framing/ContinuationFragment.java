package com.blue_unicorn.android_auth_lib.transport_specific_bindings.bluetooth_low_energy.framing;

public interface ContinuationFragment {

    byte getSEQ();

    byte[] getDATA();
}
