package com.blue_unicorn.android_auth_lib.ctap2.transport_specific_bindings.ble.framing.data;

/*
 * Represents frames that contain the information sent via bluetooth low energy.
 * Represents request frames as well as response frames, as the only difference in the CTAP2 specs is the mnemonic of the CMD/STAT field.
 * See <a href="https://fidoalliance.org/specs/fido-v2.0-ps-20190130/fido-client-to-authenticator-protocol-v2.0-ps-20190130.html#ble-framing">corresponding section of the CTAP2 specification</>.
 */
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
