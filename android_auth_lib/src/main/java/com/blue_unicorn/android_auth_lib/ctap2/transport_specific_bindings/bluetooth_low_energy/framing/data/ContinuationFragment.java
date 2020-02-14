package com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.bluetooth_low_energy.framing.data;

public interface ContinuationFragment extends Fragment {

    byte getSEQ();

    void setSEQ(byte SEQ);
}
