package com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.bluetooth_low_energy.framing.data;

public interface InitializationFragment extends Fragment {

    byte getCMD();

    void setCMD(byte CMD);

    byte getHLEN();

    void setHLEN(byte HLEN);

    byte getLLEN();

    void setLLEN(byte LLEN);
}
