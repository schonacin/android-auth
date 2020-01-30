package com.blue_unicorn.android_auth_lib.transport_specific_bindings.bluetooth_low_energy.framing.data;

public interface Frame {

    byte getCMDSTAT();

    byte getHLEN();

    byte getLLEN();

    byte[] getDATA();
}
