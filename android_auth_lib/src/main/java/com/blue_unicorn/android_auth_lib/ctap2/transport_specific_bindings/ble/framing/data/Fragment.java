package com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.framing.data;

public interface Fragment {

    byte[] getDATA();

    byte[] asBytes();

}
