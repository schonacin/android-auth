package com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.bluetooth_low_energy.framing.data;

public interface Frame {

    byte getCMDSTAT();

    void setCMDSTAT(byte CMDSTAT);

    byte getHLEN();

    void setHLEN(byte HLEN);

    byte getLLEN();

    void setLLEN(byte LLEN);

    byte[] getDATA();

    void setDATA(byte[] DATA);
}
